Side Window Infotainment
=============

[![Build Status](https://travis-ci.org/cfengler/amos-ws17-proj2.svg?=branch=master)](https://travis-ci.org/cfengler/amos-ws17-proj2)


A car infotainment system, which automatically displays information about Points of Interest to back-seat passengers.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

#### Hardware
* Intel RealSense SR300
* Webcam
* GPS Module


#### Software
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* .NET Framework 4.5 (should already be available on Win10)
* [Intel RealSense SDK 2016 R2](https://software.intel.com/en-us/realsense-sdk-windows-eol)

#### API Keys

Google APIs:

* Setup a project with the [Google Cloud Console][cloud-console] and create an API key
* On the dashboard, enable the following services:
	* Cloud Vision API
	* Places API
	* Knowledge Graph Search API
* You can create API keys for each service or use one for all
* Insert your API keys in `api-config.cfg`

JavoNet:

* Request a license at [JavoNet][javonet], you can also use the trial version for now
* Insert the e-mail you have used for registration and the license key in `api-config.cfg`

[cloud-console]: https://console.cloud.google.com
[vision-api]: https://console.cloud.google.com/apis/api/
[javonet]: https://www.javonet.com/

### Installing

There a additional steps needed to setup your development environment.

1. In order to make the GPS module work, you need to download the **RXTX Windows x64 binaries** from [here](http://fizzed.com/oss/rxtx-for-java)
  * Copy `RXTXcomm.jar` to `<JAVA_HOME>\jre\lib\ext`
  * Copy `rxtxSerial.dll` to `<JAVA_HOME>\jre\bin`
  * Copy `rxtxParallel.dll` to `<JAVA_HOME>\jre\bin`
2. The model graph and labels of the classifier are not bundled into the repository, you can download them using Maven:

	```
	mvn compile
	```
	
## Deployment

Build and deploy the `jar` using Maven
```
mvn package
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Tensorflow](https://www.tensorflow.org/) - Machine Learning Framework
* [JavaCV](https://github.com/bytedeco/javacv) - Java Interface to OpenCV
* [Google API Client](https://developers.google.com/api-client-library/) - API Client for Google Services


## Versioning

For the versions available, see the [tags on this repository](https://github.com/cfengler/amos-ws17-proj2/tags). 


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE/license.md) file for details