
# HelloPi

A simple HelloFX application with Java 11+, JavaFX 11+ and GraalVM that runs on HotSpot or as a native image on Desktop and Embedded devices (Raspberry Pi).

## Documentation

Read about getting started with JavaFX on Embedded [here](https://docs.gluonhq.com/#platforms_embedded)

Note that Gluon applications can run on JVM/HotSpot on ARM devices (32/64 bits) but native images can be created only for AArch64 (64 bits).

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
   
### Embedded (Raspberry Pi 32/64 bits)
    
Run the application on JVM/HotSpot using:

    mvn gluonfx:run -Ppi,sdk

### Embedded (Raspberry Pi 64 bits)

On a Linux machine, build a native image using:
    
    mvn gluonfx:build -Ppi
    
From the Linux machine, install the native image, making sure the `remote.host.name` and `remote.dir` properties are set correctly:
    
    mvn gluonfx:install -Ppi
 
and run remotely with SSH:
   
    mvn gluonfx:nativerun -Ppi

or locally from the Raspberry Pi:

    export ENABLE_GLUON_COMMERCIAL_EXTENSIONS=true
    /home/pi/Downloads/samples/hellpi/HelloPi -Dmonocle.platform=EGL -Duse.egl=true -Dembedded=monocle -Dglass.platform=Monocle
