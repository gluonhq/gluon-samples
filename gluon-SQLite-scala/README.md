
GluonSQLite
===========

A JavaFX Application that shows how to embed an SQLite database on your project.

- You can bundle an existing database file as a resource
- You can create by code a new database
- You can perform any query over the resulting database

It uses the proper SQLite dependency and driver for each platform, and uses Gluon Charm Down to 
access the private storage location on them

Instructions
------------
To execute the sample, do as follows:

* Desktop
> Just run it from your IDE or from command line: `./gradlew run`
* Android
> Connect your Android device and run `./gradlew androidInstall`
* iOS
> Connect your iOS device and run `./gradlew launchIOSDevice`
* Embedded
> Configure your Raspberry Pi, add the settings to the `build.gradle` script and run `./gradlew runEmbedded`

Documentation
-------------

Read how to create this sample step by step [here](http://docs.gluonhq.com/samples/gluonsqlite/)

