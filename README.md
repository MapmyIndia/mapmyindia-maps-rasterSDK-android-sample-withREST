![MapmyIndia APIs](https://www.mapmyindia.com/api/img/mapmyindia-api.png)
# MapmyIndia Raster Map Android SDK

## Getting Started

MapmyIndia.s Android Map Raster SDK helps to embed MapmyIndia maps within your Android application. Through customized raster tiles, you can add different map layers to your application and add bunch of controls and gestures to enhance map usability thus creating potent map based solutions for your customers.

## API Usage

Your MapmyIndia Maps SDK usage needs a set of license keys (get them [here](http://www.mapmyindia.com/api/signup)) and is governed by the API [terms and conditions](http://www.mapmyindia.com/api/terms-&-conditions). As part of the terms and conditions, **you cannot remove or hide the MapmyIndia logo and copyright information** in your project.  
  
The allowed SDK hits are described on the [plans](http://www.mapmyindia.com/api/pricing) page. Note that your usage is shared between platforms, so the API hits you make from a web application, Android app or an iOS app all add up to your allowed daily limit.

## Setup your project

Follow these steps to add the SDK to your project –

-   Create a new project in Android Studio
-   Add MapmyIndia repository in your project level ``build.gradle``
```groovy
allprojects {  
    repositories {  
    
        maven {  
            url 'https://maven.mapmyindia.com/repository/mapmyindia/'  
  }  
    }  
}
```
-   Add below dependency in your app-level ``build.gradle``
```groovy
implementation 'com.mapmyindia.sdk:mapmyindia-android-raster-sdk:2.5.1'
```
-   Add the following permissions to your AndroidManifest.xml file

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/> 
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> 
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> 
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
- Add these permissions in your project

**Add Java 8 support in your project**
_Add following lines in your app module's ``build.gradle``_
```groovy
compileOptions {
      sourceCompatibility 1.8
      targetCompatibility 1.8
}
```
### Add your API keys to the SDK -  
The grant type is "client_credentials" (without quotes).
```java
MapmyIndiaAccountManager.getInstance().setRestAPIKey(getRestAPIKey()); 
MapmyIndiaAccountManager.getInstance().setMapSDKKey(getMapSDKKey()); 
MapmyIndiaAccountManager.getInstance().setAtlasGrantType(getAtlasGrantType()); 
MapmyIndiaAccountManager.getInstance().setAtlasClientId(getAtlasClientId()); 
MapmyIndiaAccountManager.getInstance().setAtlasClientSecret(getAtlasClientSecret());
```

You cannot use the MapmyIndia Map Mobile SDK without these function calls. You will find your keys in your [API Dashboard](http://www.mapmyindia.com/api/dashboard).


For any queries and support, please contact: 

![Email](https://www.google.com/a/cpanel/mapmyindia.co.in/images/logo.gif?service=google_gsuite) 
Email us at [apisupport@mapmyindia.com](mailto:apisupport@mapmyindia.com)

![](https://www.mapmyindia.com/api/img/icons/stack-overflow.png)
[Stack Overflow](https://stackoverflow.com/questions/tagged/mapmyindia-api)
Ask a question under the mapmyindia-api

![](https://www.mapmyindia.com/api/img/icons/support.png)
[Support](https://www.mapmyindia.com/api/index.php#f_cont)
Need support? contact us!

![](https://www.mapmyindia.com/api/img/icons/blog.png)
[Blog](http://www.mapmyindia.com/blog/)
Read about the latest updates & customer stories


> © Copyright 2019. CE Info Systems Pvt. Ltd. All Rights Reserved. | [Terms & Conditions](http://www.mapmyindia.com/api/terms-&-conditions)
> mapbox-gl-native copyright (c) 2014-2019 Mapbox.
