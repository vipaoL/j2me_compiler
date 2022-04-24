/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package serverscript;

import java.io.ByteArrayOutputStream;

import java.rmi.RemoteException;

import javax.servlet.http.HttpSession;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/** Server functionality implementation. */
public class Implementation implements Interface, ServiceLifecycle {
    private static final int ID_START = 0;
    private static final int ID_INTRO = 1;
    private static final int ID_MAIN_MENU = 2;
    private static final int ID_SELECT_TOPIC = 3;
    private static final int ID_NEWS1 = 4;
    private static final int ID_NEWS2 = 5;
    private static final int ID_NEWS3 = 6;
    private static final int ID_EXIT = 7;
    private static final int ID_ERROR = 8;

    /**
     * Cache string used by <code>news2()</code> function.
     * @see #news2
     */
    private static String news2Cache = null;

    /** Per thread document builder factory object. */
    private DocumentBuilderFactory factory;

    /** Per thread document builder object. */
    private DocumentBuilder builder;

    /** Underlying implementation context. */
    private ServletEndpointContext context;

    /** Initialize service instance. */
    public void init(java.lang.Object context) {
        factory = DocumentBuilderFactory.newInstance();

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            builder = null;
        }

        this.context = (ServletEndpointContext)context;
    }

    /** Destroy service instance. */
    public void destroy() {
        factory = null;
        builder = null;
        context = null;
    }

    /** Process server request. */
    public String request(String info, String command)
        throws RemoteException {
        HttpSession session = context.getHttpSession();
        Integer idobj = (Integer)session.getAttribute("id");
        int id = ((idobj == null) || ((info == null) && (command == null))) ? 0 : idobj.intValue();
        id = processCommandAction(id, info, command);
        session.setAttribute("id", new Integer(id));

        return createResponse(id);
    }

    /** Check client state and select response id. */
    private int processCommandAction(int id, String info, String command)
        throws RemoteException {
        if ("Exit".equals(command)) {
            return ID_EXIT;
        }

        switch (id) {
        case ID_START:
            return ID_INTRO;

        case ID_INTRO:
            return ID_MAIN_MENU;

        case ID_NEWS1:
        case ID_NEWS2:
        case ID_NEWS3:
            return ID_MAIN_MENU;

        case ID_MAIN_MENU:

            if ("Science & Technology headlines".equals(info)) {
                return ID_NEWS1;
            }

            if ("Breaking News headlines".equals(info)) {
                return ID_NEWS2;
            }

            if ("Fairy Tales headlines".equals(info)) {
                return ID_NEWS3;
            }

        // fall through
        default:
            return ID_ERROR;
        }
    }

    /** Create service response by id. */
    public String createResponse(int id) throws RemoteException {
        try {
            switch (id) {
            case ID_INTRO:
                return intro();

            case ID_MAIN_MENU:
                return menu();

            case ID_EXIT:
                return exit();

            case ID_NEWS1:
                return news1();

            case ID_NEWS2:
                return news2();

            case ID_NEWS3:
                return news3();

            case ID_ERROR:default:
                return error();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.toString());
        }
    }

    /**
     * Intro screen.
     * @return intro screen xml description.
     * */
    private String intro() throws Exception {
        Document doc = builder.newDocument();
        Element root =
            createAlert(doc, "Jsr172 demo", "info",
                "Welcome to jsr172 demo.\n" +
                "All information displayed there comes from server.\n\n" +
                "Demo displays fake news from several news sources");
        addCommand(doc, root, "Ok", "ok");

        return string(doc);
    }

    /**
     * Main menu screen.
     * @return main menu screen xml description.
     */
    private String menu() throws Exception {
        Document doc = builder.newDocument();
        Element root = createRoot(doc, "List", "Select news source");
        addItem(doc, root, "Science & Technology headlines");
        addItem(doc, root, "Breaking News headlines");
        addItem(doc, root, "Fairy Tales headlines");
        addCommand(doc, root, "Exit", "exit");

        Element select = addCommand(doc, root, "Select", "item");
        select.setAttribute("select", "true");

        return string(doc);
    }

    /**
     * News1 screen. This screen is represented by string.
     * @return news1 screen xml description.
     */
    private String news1() throws Exception {
        return "<List title=\"Science &amp; Technology headlines\">" +
        "<Item>Science &amp; Technology: WTK Powers Mars Landing</Item>" +
        "<Item>Science &amp; Technology: 3 Million Developers at WTK Convention</Item>" +
        "<Item>Science &amp; Technology: DefaultColorPhone 3.0 Launched in U.S.A.</Item>" +
        "<Command select=\"true\" title=\"Back\" type=\"ok\"/>" +
        "<Command title=\"Exit\" type=\"exit\"/>" + "</List>";
    }

    /** News2 screen. This screen generated as xml document using helper
     * functions. Resulting xml string cached.
     * @return news2 screen xml description.
     */
    private String news2() throws Exception {
        if (news2Cache != null) {
            return news2Cache;
        }

        Document doc = builder.newDocument();
        Element root = createRoot(doc, "List", "Breaking News headlines");
        addItem(doc, root, "Breaking News: Weather Forecast Accurate");
        addItem(doc, root, "Breaking News: Pigs in Formation Flying Contest");
        addItem(doc, root, "Breaking News: Undersea explorers find Atlantis");
        addCommand(doc, root, "Exit", "exit");

        Element select = addCommand(doc, root, "Back", "ok");
        select.setAttribute("select", "true");

        return news2Cache = string(doc);
    }

    /**
     * News3 screen. The screen generated as xml document.
     * @return news3 screen xml description.
     */
    private String news3() throws Exception {
        Document doc = builder.newDocument();

        // create root element
        Element root = doc.createElement("List");
        doc.appendChild(root);
        root.setAttribute("title", "Fairy Tales headlines");

        // adding element 1
        Element element1 = doc.createElement("Item");
        Text text1 = doc.createTextNode("Fairy Tales: Mary's Lamb Uses Peroxide");
        root.appendChild(element1);
        element1.appendChild(text1);

        // adding element 2
        Element element2 = doc.createElement("Item");
        Text text2 = doc.createTextNode("Fairy Tales: Humpty Dumpty Sets Wall-Sitting Record");
        root.appendChild(element2);
        element2.appendChild(text2);

        // adding element 3
        Element element3 = doc.createElement("Item");
        Text text3 = doc.createTextNode("Fairy Tales: Woodman Saves Red Riding Hood, Grandmother");
        root.appendChild(element3);
        element3.appendChild(text3);

        // adding back command
        Element command1 = doc.createElement("Command");
        command1.setAttribute("title", "Back");
        command1.setAttribute("type", "ok");
        root.appendChild(command1);

        // adding exit command
        Element command2 = doc.createElement("Command");
        command2.setAttribute("title", "Exit");
        command2.setAttribute("type", "exit");
        root.appendChild(command2);

        // convert to string and return
        DOMSource ds = new DOMSource(doc);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult sr = new StreamResult(os);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(ds, sr);

        return new String(os.toByteArray(), "UTF-8");
    }

    /**
     * Server error screen.
     * @return screen error xml description string.
     */
    private String error() throws Exception {
        Document doc = builder.newDocument();
        Element root =
            createAlert(doc, "Server error", "error", "Server unable to process your request.");
        addCommand(doc, root, "Exit", "exit");

        return string(doc);
    }

    /**
     * Client exit screen.
     * @return xml string requesting client to exit.
     */
    private String exit() throws Exception {
        Document doc = builder.newDocument();
        Element root = doc.createElement("Exit");
        doc.appendChild(root);

        return string(doc);
    }

    //////////// service functions //////////////

    /**
     * Service function for converting document to string.
     * @param document input document.
     * @return string produced from xml document.
     */
    private String string(Document document) throws Exception {
        DOMSource ds = new DOMSource(document);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult sr = new StreamResult(os);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(ds, sr);

        return new String(os.toByteArray(), "UTF-8");
    }

    /**
     * Service function producing Alert root element with specified
     * title string, type, and message. Adds it to current document.
     * @param doc current document.
     * @param title Alert title string.
     * @param type Alert type.
     * @param msg Alert message.
     * @return Resulting root element of the document.
     */
    private Element createAlert(Document doc, String title, String type, String msg) {
        Element root = doc.createElement("Alert");
        doc.appendChild(root);
        root.setAttribute("title", title);
        root.setAttribute("type", type);

        Text text = doc.createTextNode(msg);
        root.appendChild(text);

        return root;
    }

    /**
     * Service function producing root element of specified type with specified
     * title string. Adds it to current document.
     * @param doc current document.
     * @param type root element type.
     * @param title title property of the element.
     * @return Resulting root element of the document.
     */
    private Element createRoot(Document doc, String type, String title) {
        Element root = doc.createElement(type);
        doc.appendChild(root);
        root.setAttribute("title", title);

        return root;
    }

    /**
     * Service function producing element describing List item. Adds it to
     * root element of current document.
     * @param doc current document.
     * @param root root element of current document.
     * @param item name of item to be added to root element.
     * @return Resulting element.
     */
    private Element addItem(Document doc, Element root, String item) {
        Element element = doc.createElement("Item");
        Text text = doc.createTextNode(item);
        root.appendChild(element);
        element.appendChild(text);

        return element;
    }

    /**
     * Service function producing element describing command. Adds it to
     * root element of current document.
     * @param doc current document.
     * @param root root element of current document.
     * @param title command title.
     * @param type command type.
     * @return Resulting element.
     */
    private Element addCommand(Document doc, Element root, String title, String type) {
        Element command = doc.createElement("Command");
        command.setAttribute("title", title);
        command.setAttribute("type", type);
        root.appendChild(command);

        return command;
    }
}
