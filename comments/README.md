# Comments

 A JavaFX Application that uses Gluon Mobile: 

 - CloudLink Client: the client-side counterpart of the Gluon CloudLink service 
 - Glisten: Simple application framework, custom UI controls and customized existing JavaFX UI controls

## Prerequisites

You need a valid subscription to Gluon CloudLink. You can get it [here](https://gluonhq.com/products/cloudlink/buy/), and there is a 30-day free trial.
Sign up and get a valid account on Gluon CloudLink and a link to access the Gluon Dashboard. 

Open the [Gluon Dashboard](https://gluon.io) in your browser and sign in using the Gluon account credentials provided above.
Go to the Credentials link, and under the Client tab you will find a pair of key/secret tokens.
Download the file `gluoncloudlink_config.json` and store it in the `client` project under the `src/main/resources/` folder.

## Documentation

Read how to create this sample step by step [here](https://docs.gluonhq.com/samples/comments/)

## Quick Instructions

We use [GluonFX plugin](https://docs.gluonhq.com/) to build a native image for platforms including desktop, android and iOS.
Please follow the prerequisites as stated [here](https://docs.gluonhq.com/#_requirements).

### Desktop

Run the application on JVM/HotSpot:

    mvn gluonfx:run

Run the application and explore all scenarios to generate config files for the native image with:

    mvn gluonfx:runagent

Build a native image using:

    mvn gluonfx:build

Run the native image app:

    mvn gluonfx:nativerun

### Android

Build a native image for Android using:

    mvn gluonfx:build -Pandroid

Package the native image as an 'apk' file:

    mvn gluonfx:package -Pandroid

Install it on a connected android device:

    mvn gluonfx:install -Pandroid

Run the installed app on a connected android device:

    mvn gluonfx:nativerun -Pandroid

### iOS

Build a native image for iOS using:

    mvn gluonfx:build -Pios

Install and run the native image on a connected iOS device:

    mvn gluonfx:nativerun -Pios

Create an IPA file (for submission to TestFlight or App Store):

    mvn gluonfx:package -Pios
