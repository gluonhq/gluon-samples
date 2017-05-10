
Spring - Message Of The Day
===========

This is a sample application demonstrating how to extend an existing web application with a mobile application
 component. The web application uses [Gluon CloudLink](http://gluonhq.com/products/cloudlink), the
 [Spring Framework](http://spring.io) and [Spring Boot](http://projects.spring.io/spring-boot/).

## Application components

The sample is split up in two different components:

 * server: the web application, connecting with Gluon CloudLink
 * client: the mobile application

## Prerequisites

You need a valid subscription to Gluon CloudLink. You can get it [here](http://gluonhq.com/products/cloudlink/buy/), and 
there is a 30-day free trial. Sign up and get a valid account on Gluon CloudLink and a link to access the Gluon 
Dashboard. 

Open the Dashboard in your browser, and sign in using the Gluon account credentials provided above. 
Go to the Credentials link, and under the Client tab you will find a pair of key/secret tokens. 
Download the file `gluoncloudlink_config.json` and store it under the `client` project `src/main/resources/` folder. 

Also, under the Credentials link, Server tab, you'll find the `Server Key` required below.

### Running the server application locally

Assign the `Server Key` to the `GCL_SERVER_KEY` and run the server:

./gradlew :server:bootRun

#### Run the mobile application

To run the mobile application on your desktop, use the following command:

* Desktop
> Just run it from your IDE or from command line: `./gradlew client:run`
* Android
> Connect your Android device and run `./gradlew client:androidInstall`
* iOS
> Connect your iOS device and run `./gradlew client:launchIOSDevice`

Documentation
-------------

Read how to create this sample step by step [here](http://docs.gluonhq.com/samples/springmotd/)

