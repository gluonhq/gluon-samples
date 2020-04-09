# Gluon Connect - Basic Usage

A JavaFX application that uses [Gluon Connect](https://gluonhq.com/open-source/connect), an application framework to
bind your data to your JavaFX UI controls. This application demonstrates how to retrieve data from a resource that is
located on the classpath.

## Documentation

Read how to create this sample step by step here

## Quick Instructions

Please follow the pre-requisites as stated [here](https://github.com/gluonhq/client-samples/#build-and-run-the-samples).

### Desktop

Run the application using:

    mvn javafx:run

Build native image using:

    mvn client:build

Run native image app:

    mvn client:run

### Android

Build using:

    mvn client:build -Pandroid

Package the android application:

    mvn client:package -Pandroid

Install it on a connected android device:

    mvn client:install -Pandroid

Run installed application on the connected android device:

    mvn client:run -Pandroid

### iOS

Build using:

    mvn client:build -Pios

Package the iOS application:

    mvn client:package -Pios

Install it on a connected iOS device:

    mvn client:install -Pios

Run installed application on the connected iOS device:

    mvn client:run -Pios
