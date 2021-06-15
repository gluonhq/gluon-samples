# Hello Tribuo

This Gluon Client sample was generated from https://start.gluon.io

## Basic Requirements

A list of the basic requirements can be found online in the [Gluon documentation](https://docs.gluonhq.com/#_requirements).

## Quick instructions

### Run the sample

Run the application on JVM/HotSpot:

    mvn gluonfx:run

### Run the sample as a native image

Run the application and explore all scenarios to generate config files for the native image with:

    mvn gluonfx:runagent
    
Build and run the native image:

    mvn gluonfx:build gluonfx:nativerun

### Run the sample as a native android image

    mvn -Pandroid gluonfx:build gluonfx:package gluonfx:install gluonfx:nativerun

### Run the sample as a native iOS image

    mvn -Pios gluonfx:build gluonfx:package gluonfx:install gluonfx:nativerun

## Selected features

This is a list of all the features that were selected when creating the sample:

### JavaFX 15 Modules

 - javafx-base
 - javafx-graphics
 - javafx-controls

### Gluon Features

 - Glisten: build platform independent user interfaces
 - Attach display
 - Attach lifecycle
 - Attach statusbar
 - Attach storage
