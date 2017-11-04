# SWIT - Image Analysis Package

## Setup

* Create a project with the [Google Cloud Console][cloud-console], and enable
  the [Vision API][vision-api].
* Create an API key for Cloud Vision
* Insert API key in DetectLandmark.java for testing

	```java
	private static final String CLOUD_VISION_API_KEY = "YOUR_API_KEY";
	```

[cloud-console]: https://console.cloud.google.com
[vision-api]: https://console.cloud.google.com/apis/api/

## Dependencies
* [Google Cloud Vision API](https://cloud.google.com/vision/?hl=de)
* [Google APIs Client](https://mvnrepository.com/artifact/com.google.api-client/google-api-client)
* [Google Guava](https://github.com/google/guava)
* JUnit 4.12

