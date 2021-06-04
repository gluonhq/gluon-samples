# Gluon Connect - Rest Provider

A JavaFX application that uses [Gluon Connect](https://github.com/gluonhq/connect), a mobile application
framework to bind your data to your JavaFX UI controls. This application demonstrates how to retrieve data from an
online HTTP resource.

## Documentation

Read more about this particular sample in the [REST provider](https://docs.gluonhq.com/connect/latest/#_rest_provider)
section on the Gluon Connect documentation website.

## Quick Instructions

We use [GluonFX plugin](https://docs.gluonhq.com/) to build a native image for platforms including desktop, android and iOS.
Please follow the prerequisites as stated [here](https://docs.gluonhq.com/#_requirements).

### Desktop

Run the application using:

    mvn javafx:run

Build a native image using:

    mvn gluonfx:build

Run the native image app:

    mvn gluonfx:run

### Android

Build a native image for Android using:

    mvn gluonfx:build -Pandroid

Package the native image as an 'apk' file:

    mvn gluonfx:package -Pandroid

Install it on a connected android device:

    mvn gluonfx:install -Pandroid

Run the installed app on a connected android device:

    mvn gluonfx:run -Pandroid

### iOS

Build a native image for iOS using:

    mvn gluonfx:build -Pios

Install and run the native image on a connected iOS device:

    mvn gluonfx:run -Pios

Create an IPA file (for submission to TestFlight or App Store):

    mvn gluonfx:package -Pios
