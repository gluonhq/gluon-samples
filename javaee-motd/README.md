
JavaEE - Message Of The Day
===========

This is a sample application demonstrating how to extend an existing web application with a mobile application
 component. The web application uses [Gluon CloudLink](http://gluonhq.com/products/cloudlink) and the
 JavaEE Framework.

## Application components

The sample is split up in two different components:

 * server: the web application, connecting with Gluon CloudLink
 * client: the mobile application

## Prerequisites

You need a valid subscription to Gluon CloudLink. You can get it [here](http://gluonhq.com/products/cloudlink/buy/), and 
there is a 30-day free trial. Sign up and get a valid account on Gluon CloudLink and a link to access the Gluon 
Dashboard. 

Open the [Gluon Dashboard](https://gluon.io) in your browser and sign in using the Gluon account credentials
provided above. Go to the Credentials link, and under the Client tab you will find a pair of key/secret tokens. 
Download the file `gluoncloudlink_config.json` and store it in the `client` project under the `src/main/resources/`
folder. 

Also from the Credentials link, Server tab, you'll find the `Server Key` that is required below.

### Running the server application locally

Update the `GLUON_SERVER_KEY` variable in the `GluonService` class with the `Server Key` of your Gluon CloudLink
application and start the server:

./gradlew :server:run

You can now navigate with a browser to http://localhost:8080/motd-server

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

Read how to create this sample step by step [here](http://docs.gluonhq.com/samples/javaeemotd/)

