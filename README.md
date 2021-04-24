# Robot Recharge
A programming game where the goal is to guide a robot through various levels using programmatic controls.

## Features
- Multi-language programming support (PHP, Python, JavaScript, Lua, Ruby, BASIC, and Google Blockly) 
- Custom code editor
- Cloud Saves
- Tutorial Levels
- Level fast-forwarding
- UI Themes 
- Robotic Text to Speech for code debugging
- Android Rewarded ads for level solutions

## Links
[Web Demo](https://thelogicmaster.github.io/robot-recharge/)

[![Get it on Google Play](https://badgen.net/badge/icon/googleplay?icon=googleplay&label)](https://play.google.com/store/apps/details?id=com.thelogicmaster.robot_recharge)

[GameJolt Page](https://gamejolt.com/games/robot-recharge/570956)

## Screenshots
<img src="media/screenshot1.png?raw=true" height="336" width="598">
<img src="media/screenshot2.png?raw=true" height="336" width="598">
<img src="media/screenshot3.png?raw=true" height="336" width="598">

## Technical Info
### Utilized Libraries
- [LibGDX](https://libgdx.com/) (Game framework)
- [Blockly](https://developers.google.com/blockly) (Graphical programming)
- [Jython](https://www.jython.org/) (Desktop Python)
- [Chaquopy](https://chaquo.com/chaquopy) (Android Python)
- [LuaJ](https://github.com/luaj/luaj) (Lua)
- [Quercus](https://www.caucho.com/resin-3.1/doc/quercus.xtp) (PHP)
- [Duktape Android](https://github.com/TheLogicMaster/duktape-android) (Android JavaScript)
- [duktape4j](https://github.com/TheLogicMaster/duktape4j) (Desktop JavaScript)
- [Jasic](https://github.com/munificent/jasic) (BASIC interpreter base code)
- [JRuby](https://github.com/jruby/jruby) (Ruby)
- [MaryTTS](https://github.com/marytts/marytts) (Desktop text to speech)
- [Android MaryTTS](https://github.com/AndroidMaryTTS/AndroidMaryTTS) (Android text to speech)
- [Project Lombok](https://projectlombok.org/) (Boilerplate generation)
- [JCEF](https://github.com/chromiumembedded/java-cef) (Desktop embedded browser)
- [JavaPackager](https://github.com/fvarrui/JavaPackager) (Gradle plugin to package desktop builds)

## Todo
- Debug/step through mode
- Tab to double-space functionality in editor
- Make the UI not terrible
- Mobile HTML custom keyboard (Or not if it's not worth it and just disable code editor on mobile browsers)
- Code testing (JS code transformations, for instance)
- Level editor
- Prevent Firefox '/' quick search functionality
- Robot customization
- Android separate execution process using synchronous AIDL. Would provide a sandbox and the ability to interrupt infinite loops. 

## Building
### GitHub Actions
The github actions workflow for the project builds and packages the releases for each platform, in addition to deploying
the web demo to the gh-pages branch, and the Android application to the Google Play Store.

### Building From Source
Building the project requires several external libraries depending on which modules you want to build. Regardless of
which platform you are building, the Android SDK is required to compile to project. To build without it, it would be
necessary to comment out parts of `settings.gradle` and `build.gradle` at the project root. To enable debug mode 
(mostly logging) for the entire project, add `debug=true` to a `local.properties` file at the root for the project. 

For the Desktop module, the respective [JCEF library](https://github.com/jcefbuild/jcefbuild/releases) for your platform is required at
`<project>/Libraries/jcef/<linux64|win64|mac64>` if you intend to use the Google Blockly functionality. To run without 
Blockly support, simply run the `desktop:run` gradle task. To run with Blockly, the `JCEFDesktopLauncher` class needs to
be run specifically with the VM option: 
`-Djava.library.path=<project>/Libraries/jcef/<platform>/:<project>/desktop/natives/<platform>`. To package the 
desktop project, a JRE is needed at `<project>/Libraries/jre/<platform>` or the `jrePath` values need to be changed in
`<project>/desktop/build.gradle` tasks, and not setting it should use the system default.

For the Android module, a [Chaquopy](https://chaquo.com/chaquopy) key is required in the `local.properties` file. The 
normal Android building methods should work as long as the Android SDK is installed. Google Play Services need to be
setup in addition to setting the AdMob IDs in the `AndroidGameServies` class.

For the HTML subproject, there aren't any additional dependencies. To run the game in a browser, run the `html:superDev`
gradle task and open `localhost:8080` in a browser. To package it, just run `html:dist`. 

For IOS, it works, but requires the whole OSX setup and developer account for more than running on a test device. 
Blockly and text-to-speech aren't supported at present.

For GameJolt cloud support, a `gameJoltKey` from GameJolt needs to be set in the `local.properties` file.

### Updating Blockly
To build and copy Blockly, run the `updateBlockly` task.

### Updating JCEF
To update to a new JCEF release, download the platform assets from [jcefbuild](https://github.com/jcefbuild/jcefbuild/releases). 
The 6 native platform JARs in the release archives need to have their native libraries extracted to the `desktop/natives` 
directory with the existing format. The `desktop/libs` directory should have its contents updated from one of the new platform 
release archives. The `Libraries/jcef` directory needs to be updated using the archive `bin/lib` directories.
