# Gluon Connect - Basic Usage

A JavaFX application that uses [Gluon Connect](https://gluonhq.com/open-source/connect),
an application framework to bind your data to your JavaFX UI controls. 
This application demonstrates how to retrieve data from a resource that is located on the classpath.

## Documentation

Read more about this particular sample in the [Creating a View](https://docs.gluonhq.com/charm/latest/#_creating_a_view)
section on the Gluon Mobile documentation website.

## Quick Instructions

We use [GluonFX plugin](https://docs.gluonhq.com/) to build a native image for platforms including desktop, android and iOS.
Please follow the pre-requisites as stated [here](https://docs.gluonhq.com/#_requirements).

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
