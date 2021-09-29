# HelloWeb Application

Gluon Application on the browser

## Basic Requirements

A list of the basic requirements can be found online in the [Gluon documentation](https://docs.gluonhq.com/#_requirements).

## Quick instructions

### Run the sample

    mvn gluonfx:run

### Run the sample in browser

    mvn gluonfx:build gluonfx:nativerun -Pweb

NOTE: Gluon currently supports Chrome, Chromium, and Firefox browser.

### Desktop

Build a native image using:

    mvn gluonfx:build

Run the native image app:

    mvn gluonfx:nativerun