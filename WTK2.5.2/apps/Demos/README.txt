--------------------------------------------------------------------------------
     Mobile Information Device Profile (MIDP) 2.0 (JSR 118) Demonstration
--------------------------------------------------------------------------------

1. Introduction

    This demo contains several MIDlets that highlight different MIDP features.
    
2. Demos

    2.1 Colors
        This application displays a large horizontal rectangle that runs 
        the width of the screen. Below, ten small vertical rectangles span 
        the screen. Finally, three horizontal color bars indicate values for 
        blue, green, and red (RGB). Values are expressed as decimal (0-255) or 
        hexadecimal (00-ff) based on the first menu selection.

        - To select a vertical bar to change, use the up navigation arrow 
          to move to the color bars. Use the right navigation arrow to highlight 
          a color bar. The large rectangle becomes the color of the selected bar.
        - Use the up or down selection arrows to choose the value to change 
          (red, green, or blue). Use the left or right arrow keys to increase or 
          decrease the selected value. The second menu item allows you to jump 
          in increments of 4 (Fine) or 32 (coarse).
        - You can change the color on any or all of the vertical bars. 
        
    2.2 Properties
        This MIDlet displays property values.
        
    2.3 Http
        This test application uses an HTTP connection to request a web page. 
        The request is issued with HTTP protocol GET or POST methods. 
        If the HEAD method is used, the head properties are read from the request.

        Before beginning, examine your settings as follows:
        - Go to Preferences > Security. Set the policy to JTWI and the domain 
          to maximum.
        - In Preferences > Network Configuration, the HTTP version must be 1.1.
        - If you are behind a firewall, go to Preferences > Network Configuration 
          and specify your proxy server information.
        - If you are running antivirus software, you might need to create a rule 
          that allows this MIDlet to allow connections to and from a specific 
          web site.

        Running the Demo
        - Launch the Http MIDlet. By default the MIDlet attempts to contact 
          http://www.yahoo.com. To test, choose the Menu soft key and choose 
          1, 2, or 3 to test the selected URL.

        Http Test returns the information it is able to obtain. If the information 
        fills the screen use the down arrow to scroll to the end. The amount 
        of information depends on the type of request and on the amount of META 
        information the page provides. To provide body information or content, 
        the page must declare CONTENT-LENGTH as described in RFC 2616.

        Use the Menu soft key for the following actions.
        - Choose 1 to GET information from the selected page.
        - Choose 2 to obtain the POST information from the selected page.
        - Choose 3 to display the HEAD attributes for the page.
        - Choose 4 to bring up the current list of web pages. You can chose 
          a new page or add your own page to the list. To specify a new URL, 
          choose the Menu soft key and choose 4. The screen displays http://. 
          Type in the rest of the URL, making sure to end with a slash (/). 
          For example http://www.internetnews.com/. Press the OK soft button. 
          The Http Test screen shows your new URL and prompts for an action. 
          
    2.4 FontTestlet
        This MIDlet shows the various fonts available: Proportional, Regular, 
        Regular Italic, Bold Plain, and Bold Italic. Choose 1 or 2 from the menu 
        to toggle between the system font (sans serif) and the monospace font. 
        
    2.5 Stock
        Like the Http demonstration, This sample uses an HTTP connection 
        to obtain information. Use the same preparation steps as Section 2.3, Http.

    2.6 Tickets
        This demonstrates how an online ticket auction application might behave. 
        The home screen displays a ticket ticker across the top. The Choose 
        a Band field displays Alanis Morrisette by default.

        - To select a band, highlight the band name and press SELECT. Use 
          the down arrow to highlight a different band, moby, for example, then 
          press SELECT. The available auction appears.
        - To make a bid, select the Menu soft key and choose 2. Use the arrow 
          keys to move from field to field. Fill out each field. Select the Next 
          soft key. The application asks you to confirm your bid. Use the arrow 
          keys to highlight Submit then press SELECT. You receive a Confirmation 
          number. Click Bands to return to the welcome page.
        - To set an alert, select the Menu soft key and choose 3. 
          Use the navigation arrows to move to the field and type in a value 
          higher than the current bid. Select the Save soft key. You are returned 
          to the welcome page. You can trigger the alert by making a bid that 
          exceeds your alert value. Your settings determine how often the application 
          checks for changes, so the alert may not sound for a few minutes.
        - To add a band, select the Menu soft key and choose 4. Type in a band 
          name or a comma-separated list of names. Choose the Save soft key. 
          After confirmation you are returned to the welcome page. The added 
          band(s) are displayed in the Choose a Band drop down. This is only 
          a demonstration.
        - To remove a band, select the Menu soft key and choose 5. Navigate to 
          a band then choose SELECT to mark the check box. You can select 
          multiple bands. Choose the Save soft key.
        - To display the current settings for ticker display, updates, alert 
          volume, and date, select the Menu soft key and choose 6. If desired, 
          use the arrow keys and the select key to change these values. Choose 
          the Save soft key. 
          
    2.7 ManyBalls
        This MIDlet starts with one ball traveling the screen. Use the up and 
        down arrows to accelerate or decelerate the ball speed (fps). 
        Use the right or left arrows to increase or decrease the number of balls. 

3. Required APIs
    
    JSR 30 - Connected Limited Device Configuration (CLDC) 1.0
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
