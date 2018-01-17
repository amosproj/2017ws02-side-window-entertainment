[![Build Status](https://travis-ci.org/cfengler/amos-ws17-proj2.svg?=branch=master)](https://travis-ci.org/cfengler/amos-ws17-proj2)
# amos-ws17-proj2

Repository for Group 2 of the AMOS project of the TU Berlin in the winter semester 2017 / 2018

# Setting up the project with IntelliJ
- Get and install *Java SDK 8*
  - You can get it from here: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
- Install *IntelliJ IDE* with standard modules
  - It can be downloaded from here: https://www.jetbrains.com/idea/download/#section=windows
  - In order to get a student license for the ultimate version, go here: https://www.jetbrains.com/student/
- Pull project via GitHub from: https://github.com/cfengler/amos-ws17-proj2
- Get *api-config.cfg* from Google Drive: AMOS > Project Files > Config Files > api-config.cfg
  - Downloading the file does not work. Its contents need to be copied into a local file with the same name
  - save the file to: <AMOS_root>\src\main\resources\api-config.cfg
- Download *RXTX Windows x64 binaries* from: http://fizzed.com/oss/rxtx-for-java
  - Copy RXTXcomm.jar to <JAVA_HOME>\jre\lib\ext
  - Copy rxtxSerial.dll to <JAVA_HOME>\jre\bin
  - Copy rxtxParallel.dll tot <JAVA_HOME>\jre\bin
- Download and install *.NET Framework 4.5* (should be already part of Windows 10)
- Download and install *Intel RealSense SDK 2016 R2* at: https://software.intel.com/en-us/realsense-sdk-windows-eol
- Open project with IntelliJ
  - File -> Project Structure... -> Project Settings -> Project -> Project SDK
  - Set Java SDK to *Java SDK 8*
- Run main() from <AMOS_root>\src\main\java\de.tuberlin.amos.ws17.swit\application\view\ApplicationViewImplementation.java
