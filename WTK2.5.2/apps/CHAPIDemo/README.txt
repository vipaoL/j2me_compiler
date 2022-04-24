--------------------------------------------------------------------------------
                Content Handler API (JSR 211 API) Demonstration
--------------------------------------------------------------------------------

1. Introduction

    CHAPIDemo is a content browser. It maintains a list of favorites and 
    enables you to select and view various kinds of content.

2. Usage

    This demonstration uses the content handler registry, so you cannot see 
    all of its features when you use the Run button. Instead, use 
    Project > Run via OTA to install the application into the emulator. 

    After you install CHAPIDemo, it appears in the application list as Text 
    Viewer. It is a MIDlet that is a content handler for plain text. Select 
    Text Viewer and choose Launch from the soft button menu. A list of favorite 
    links appears. 
    
    - Use the navigation keys to highlight CHAPIDemo then press SELECT 
      on the emulator. The application asks if it is OK to use airtime. 
      Press the Yes soft button. A list of various types of content appears
    - Try selecting one of the Duke.png. Use the arrows to highlight the link, 
      then press SELECT to view the file. Using CHAPI, the ImageViewer MIDlet 
      is launched and displays the content.
    - The other types of content require another content handler MIDlet suite, 
      MediaHandler. To install this suite from CHAPIDemo, select 
      the MediaHandler.jad link. The AMS is invoked and leads you through 
      the installation.
    - After the MIDlet suite is installed, you can view the other types 
      of content listed in Text Viewer.


3. Required APIs
    
    JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
    JSR 211 - Content Handler API
