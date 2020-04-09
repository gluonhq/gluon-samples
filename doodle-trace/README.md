# Doodle Trace

A JavaFX application that traces a user drawing pattern and draws it back.

## Instructions

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

Package the apk file:

    mvn client:package -Pandroid

Install it on a connected android device:

    mvn client:install -Pandroid

Run installed app on connected android device:

    mvn client:run -Pandroid

### iOS

Build native file using:

    mvn client:build -Pios

Package the IPA file:

    mvn client:package -Pios

Install it on a connected iOS device:

    mvn client:install -Pios

Run installed app on connected iOS device:

    mvn client:run -Pios

## Documentation

Read how to create this sample step by step here