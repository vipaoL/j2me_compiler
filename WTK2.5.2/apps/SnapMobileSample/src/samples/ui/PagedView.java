/* ------------------------------------------------------------------------- *
          Copyright 2004-2005 Nokia Corporation  All rights reserved.
          Nokia Mobile Phones

          Restricted Rights: Use, duplication, or disclosure by the
          U.S. Government is subject to restrictions as set forth in
          subparagraph (c)(1)(ii) of DFARS 252.227-7013, or in FAR
          52.227-19, or in FAR 52.227-14 Alt. III, as applicable.

          This software is proprietary to and embodies the
          confidential technology of Nokia Possession, use, or copying
          of this software and media is authorized only pursuant to a
          valid written license from Nokia or an authorized
          sublicensor.

          Nokia  - Wireless Software Solutions
 * ------------------------------------------------------------------------- */

package samples.ui;

import javax.microedition.lcdui.Canvas;
import java.util.Vector;

/**
 * This class implements the concept of a paged view. 
 *
 * @see View
 */
public class PagedView extends View 
{
    //--- Virtual pages ---
    protected Vector pageChildren = null;
    protected Vector namesOnPage = null;
    protected String[] pageNames;
    protected int curPage=0;
    
    /** Perform initialization for this class. */
    public static void initialize() {
    	View.initialize();
    }

    /**
     * Create a new instance of this class.
     *
     * @param canvas The canvas that this view is associated with.
     * @param name The name of this view.
     */
    public PagedView( Canvas canvas, String name) {
    	super( canvas, name);
        this.canvas = canvas;
        initView( new String[] {name});
    }
    
    /**
     * Create a new instance of this class.
     *
     * @param canvas The canvas that this view is associated with.
     * @param names An array of Strings naming the pages in this view.
     */
    public PagedView( Canvas canvas, String[] names) {
    	super( canvas, names[0]);
        this.canvas = canvas;
        initView( names);
    }
    
    private void initView( String[] names) 
    {
        this.pageNames = names;
        curPage = (names.length>1) ? -1 : 0;
    }
   
    /** 
     * Determines whether this instance has a page named "name"
     * 
     * @return boolean True if PagedView has a page with name "name" 
     */
    public boolean hasName( String name) {
    	if (pageNames == null || name == null ) return false;
    	for (int i=0; i<pageNames.length; i++) {
    		if (name.equals(pageNames[i])) return true;
    	}
    	return false;
    }

    

    /**
     * If this View implements multiple virtual "pages", <code>setPage()</code>
     * switches the active page.
     * 
     * @param pageName The name of the page to select.
     */
    public void setPage( String pageName) 
    {
    	if (pageName == null || pageNames.length <= 1) return;
    	if (pageNames.length > 0 && pageChildren == null) paginate();

    	
    	int newPage = findPageNum( pageName);
    	if (newPage==curPage) return;

    	// Change page & active page breaks:
    	setActive( false);
    	curPage = newPage;
    	children = (Vector)pageChildren.elementAt( curPage);
    	setFocus( getFirstFocusable());
    	setActive( true);
    	repaint();
    }
    
    public String[] getVisiblePages()
    {
    	return (String[])namesOnPage.elementAt(curPage);
    }

    /**
     * 
     * @param pageName
     * @return The index of the page which the given name, or -1 if not found.
     */
    protected int findPageNum( String pageName)
    {
    	for (int i=0; i<namesOnPage.size(); i++) {
    		String[] names = (String[])namesOnPage.elementAt(i);
    		for (int j=0; j<names.length; j++) {
    			if (names[j].equals(pageName)) return i;
    		}
    	}
    	return -1;
    }
    
    /**
     * 
     */
    protected boolean paginate()
    {
    	Component comp;
    	PageBreak pBreak;
    	int breakY = 0;
    	
    	if (pageNames.length==1) return true;
    	
    	curPage = -1;
    	
    	// 1. Find page breaks
    	int[] pageBreakIndxs = findPageBreakIndexes();
    
    	// 2. Calculate which ones should be broken to create new pages, and 
    	//    which should be merged together onto the same page.
    	activatePageBreaks( pageBreakIndxs);
    	
    	// 3. Group widgets onto separate pages, broken at the actived pagebreaks
    	Vector names = new Vector();
    	Vector curPage = new Vector();
    	Vector curPageNames = new Vector();
    	curPageNames.addElement( pageNames[0]);
    	pageChildren.addElement( curPage);
    	names.addElement( curPageNames);
    	int curWidget = 0;
    	breakY = 0;
    	while (curWidget < children.size()) {
    		comp = (Component)children.elementAt( curWidget);
    		if (comp instanceof PageBreak) {
    			pBreak = (PageBreak)comp;
    			if (pBreak.isActive()) {
    				breakY = pBreak.getYOffset();
    				curPage = new Vector();
    				pageChildren.addElement( curPage);
    				if (pBreak.getTitleComponent() != null) {
    					curPage.addElement( pBreak.getTitleComponent());
    				}
    				curPageNames = new Vector();
    				names.addElement( curPageNames);
    			}
    			curPageNames.addElement( pBreak.getName());
    		} else {
    			comp.setDimension( comp.getX(), comp.getY() - breakY);
    			curPage.addElement( comp);
    		}
    		
    	}
    	
    	// Convert curPageNames vectors into arrays
    	namesOnPage = new Vector();
    	for (int i=0; i<names.size(); i++) {
    		Vector n1 = (Vector)names.elementAt(i);
    		String[] n2 = new String[ n1.size()];
    		for (int j=0; j<n1.size(); j++) n2[j] = (String)n1.elementAt(j);
    		namesOnPage.addElement( n2);
    	}
        	
    	return true;
    }
    
    private int[] findPageBreakIndexes()
    {
    	Component comp;
    	int curBreak = 0;
    	
    	// 1. Find page breaks (and deactivate them all)
    	int[] pageBreakIndxs = new int[ pageNames.length-1];
    	for (int i=0; i<children.size(); i++) {
    		comp = (Component)children.elementAt(i);
    		if (comp instanceof PageBreak) {
    			pageBreakIndxs[curBreak++] = i;
    			((PageBreak)comp).setActive( false);
    		}
    		if (curBreak>pageNames.length-1) return null;
    	}
    	if (curBreak < pageNames.length-2) return null;	
    	
    	return pageBreakIndxs;
    }
    
    private void activatePageBreaks( int[] pageBreakIndxs)
    {
    	Component comp;
    	PageBreak pBreak;
    	int curBreak = 0;
    	int nextBreak = 0;
    	int breakY = 0;
    	int screenHeight = getHeight() - SOFT_BUTTON_FONT.getHeight();
    	
    	// 2. Calculate active/inactive page breaks
    	comp = null;
    	curBreak = 0;
    	while (curBreak < pageBreakIndxs.length) {
	    	do { 
	    		nextBreak++;
	    		if (nextBreak < pageBreakIndxs.length) {
	    			comp = (Component)children.elementAt( pageBreakIndxs[nextBreak]-1);
	    		} else if (nextBreak == pageBreakIndxs.length) {
	    			comp = (Component)children.lastElement();
	    		} else { 
	    			comp = null;
	    		}
	    	} while (comp != null && (comp.getY()+comp.getHeight()-breakY) < screenHeight);
	    	curBreak = nextBreak-1;
	    	if (curBreak < pageBreakIndxs.length) {
		    	pBreak = (PageBreak)children.elementAt( pageBreakIndxs[curBreak] );
		    	pBreak.setActive( true);
		    	breakY = pBreak.getYOffset(); 
	    	}
	    	curBreak++;
	    }        	
    }
       
    /**
     * Gets the name of the previous page.
     * @return The name of the previous page.
     */
    public String getPrevPage()
    {
    	if (curPage<=0) return null;
    	String[] names = (String[])namesOnPage.elementAt(curPage-1);
    	return names[0];
    }
    
    /**
     * Gets the name of the next page.
     * @return The name of the next page.
     */
    public String getNextPage()
    {
    	if (curPage>=namesOnPage.size()-1) return null;
    	String[] names = (String[])namesOnPage.elementAt(curPage+1);
    	return names[0];
    }
    
}
