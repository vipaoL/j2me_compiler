--------------------------------------------------------------------------------
          Mobile Internationalization API (JSR 238 API) Demonstration
--------------------------------------------------------------------------------

1. Introduction

    String Comparator   - shows string sorting capability
    Formatter           - shows number and date/time formatting
    MicroLexicon        - shows string and image resource retrieval


2. String Comparator example

    String comparator example demonstrates jsr238 capability of locale dependent 
    sorting of strings. String comparator is initialized for slovak language and
    for default sorting scheme. User can see different order of sorted slovak cities.


3. Formatter example

    Formatter example demonstrates jsr238 capabilities of formatting date/time and
    numbers. It loads date/time and number formatting rules and symbols from
    device resources and uses them to locale dependent formatting.


4. MicroLexicon example

    MicroLexicon demonstrates usage of device and application resources. Application
    can run localized. Ktoolbar supports locale switching: go to ktoolbar -> 
    preferences -> i18n. Enter desired locale code en-US, ja-JP, zh-CN etc. (for 
    complete list of locales look into example source code) and application starts in
    that locale.
    Application uses both kinds of resources, device resources and application resources. 
    Device resources are device dependent resources prepared by device manufacturer
    and cannot be changed by application programmer. She/he can access these resources
    which ids are known from device documentation. Example uses them for localization of
    control commands (Exit, Next, Back).
    On the other side application resources are tight to application e.q. always 
    bundled with the application. Go to ktoolbar -> Project -> i18n Resource Manager
    to edit application resources for the project. Application resources are always
    placed in "global" folder under <project>/res. Don't forget to repackage application
    jar after some editing of resource files has been done. 


5. Required APIs
    
    JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
    JSR 238 - Mobile Internationalization API


Note:
------
For right display of all characters (especially Japanese and Chinese) device
font property must be initialized with proper font. Install font into your
system, then go into WTK_HOME/wtklib/devices/<device name>/<device name>.properties
file and change default SansSerif font.
