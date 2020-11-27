# Desktop Platform

## Building
Run the dist or distBlocks task depending on whether the JCEF/blockly functionality
is desired. 

## Packaging
If blockly functionality is enabled, a JCEF build will have to be included for the target
platform. The `-Djava.library.path=/path` parameter is required when running the built jar
to tell JCEF where the native libraries are located.