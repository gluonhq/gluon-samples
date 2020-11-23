
# PushNotes

A JavaFX application that uses Gluon Mobile Glisten (a mobile application framework with mobile-friendly UI controls) and Gluon Mobile CloudLink (for cloud storage of notes and user settings) and has support for Push notifications.  

## Prerequisites

You need a valid subscription to Gluon CloudLink. You can get it [here](https://gluonhq.com/products/cloudlink/buy/), and there is a 30-day free trial.
Sign up and get a valid account on Gluon CloudLink and a link to access the Gluon Dashboard. 

Open the [Gluon Dashboard](https://gluon.io) in your browser and sign in using the Gluon account credentials provided above.
Go to the Credentials link, and under the Client tab you will find a pair of key/secret tokens.
Download the file `gluoncloudlink_config.json` and store it in project under the `src/main/resources/` folder.

## Documentation

Read how to create this sample step by step [here](https://docs.gluonhq.com/samples/pushnotes/)

## Quick Instructions

We use [Gluon Client](https://docs.gluonhq.com/client/) to build a native image for platforms including desktop, android and iOS.
Please follow the Gluon Client prerequisites as stated [here](https://docs.gluonhq.com/client/#_requirements).

### Desktop

Run the application using:

    mvn javafx:run

Build a native image using:

    mvn client:build

Run the native image app:

    mvn client:run

### Android

Build a native image for Android using:

    mvn client:build -Pandroid

Package the native image as an 'apk' file:

    mvn client:package -Pandroid

Install it on a connected android device:

    mvn client:install -Pandroid

Run the installed app on a connected android device:

    mvn client:run -Pandroid

### iOS

Build a native image for iOS using:

    mvn client:build -Pios

Install and run the native image on a connected iOS device:

    mvn client:run -Pios

Create an IPA file (for submission to TestFlight or App Store):

    mvn client:package -Pios
