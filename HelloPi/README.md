
# HelloPi

A simple HelloFX application with Java 11+, JavaFX 11+ and GraalVM that runs on HotSpot or as a native image on Desktop and Embedded devices (Raspberry Pi).

## Documentation

Read about getting started with JavaFX on Embedded [here](https://docs.gluonhq.com/#platforms_embedded)

Note that Gluon applications can run on JVM/HotSpot on ARM devices (32/64 bits) but native images currently can be created only for AArch64 (64 bits).

## Quick Instructions

We use [GluonFX plugin](https://docs.gluonhq.com/) to build a native image for platforms including desktop, embedded, android and iOS.
Please follow the prerequisites as stated [here](https://docs.gluonhq.com/#_requirements).

While running this sample on a JVM can be done directly from the Raspberry Pi (both 32 and 64 bits), the following instructions are mainly describing how to cross-compile and remote deploy a native image from a desktop Linux machine (x86_64) to a Raspberry Pi (64 bits, AArch64).

### Desktop

Testing, fixing, and development in general, is best done on desktop. Therefore, before deploying to the Raspberry Pi, you can run the application on a JVM on your desktop (x86_64) machine:

    mvn gluonfx:run

Then run the application and explore all scenarios to generate config files for the native image with:

    mvn gluonfx:runagent

Build a native image using:

    mvn gluonfx:build

Run the native image app:

    mvn gluonfx:nativerun
   
### Embedded - JVM (Raspberry Pi 32/64 bits)
    
Directly from the Raspberry Pi, run the application on a JVM using:

    mvn gluonfx:run -Ppi,sdk

### Embedded - Native (Raspberry Pi 64 bits)

#### build

On a desktop Linux machine (x86_64), cross-compile a native image for the Raspberry Pi (AArch64), using:
    
    mvn gluonfx:build -Ppi
    
#### deploy

From the same Linux machine, install the native image, making sure the `remote.host.name` and `remote.dir` properties in the sample's pom are correctly set:
    
    mvn gluonfx:install -Ppi

or, alternatively you can copy manually the native image binary that is located at:

    target/gluonfx/aarch64-linux/HelloPi    
 
### run

If you installed the native image from your Linux machine, you can run it remotely (via SSH) using:
   
    mvn gluonfx:nativerun -Ppi

In any case, you can also run it locally from the Raspberry Pi with:

    export ENABLE_GLUON_COMMERCIAL_EXTENSIONS=true
    ${remote.dir}/HelloPi -Duse.fullscreen=true -Dmonocle.platform=EGL -Dembedded=monocle -Dglass.platform=Monocle
