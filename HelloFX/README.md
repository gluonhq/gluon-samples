
# HelloFX

A simple Hello World application with Java 11+, JavaFX 15+ and GraalVM.

## Documentation

Read about this sample [here](https://docs.gluonhq.com/client/#_hellofx)

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