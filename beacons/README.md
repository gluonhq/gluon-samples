# Beacons

A JavaFX Application that shows how to use Bluetooth Low Energy service on your mobile project, both on Android and iOS.

It uses the BLE service in [Gluon Attach](https://github.com/gluonhq/attach) to access the native implementation in the mobile device.

## Documentation

Read how to create this sample step by step [here](https://docs.gluonhq.com/samples/beacons/)

## Quick Instructions

We use [Gluon Client](https://docs.gluonhq.com/client/) to build a native image for platforms including desktop, android and iOS.
Please follow the Gluon Client prerequisites as stated [here](https://github.com/gluonhq/client-samples/#build-and-run-the-samples).

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