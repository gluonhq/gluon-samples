# DeepLearning Linear Classifier 

This sample is a Java app that runs on desktop, Android and iOS devices and performs neural network training, based on [Deeplearning4j](https://github.com/deeplearning4j/deeplearning4j).

The sample is based on [this](https://deeplearning4j.org/android) one.

## Prerequisites

General prerequisites for all platforms are:

- Java 9, that can be downloaded from Oracle's [site](http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html).

- Gradle 4.3.1+, that can be downloaded from [here](https://gradle.org/releases/)

Android prerequisites:

- Android SDK that can be downloaded from [here](http://developer.android.com/sdk/index.html#Other) (only the SDK is required, Android Studio is not really necessary).
- Using the Android SDK Manager, install Android Build tools 24.0.1 or superior, SDK platform 24 or superior, and the Android Support Repository.
- Create a gradle property with the name `ANDROID_HOME`: defined in `~/.gradle/gradle.properties` that points to the Android SDK location.

iOS prerequisites:

- A Mac with MacOS X 10.11.5 or superior
- Xcode 9.2 or superior

Both iOS simulator from Xcode or a real iOS device can be used. For the latter, credentials from the Apple Developer program 
will be required to sign the app, with a valid provisioning profile (`iosProvisioningProfile`) and a valid signing identity (`iosSignIdentity`). 
For more details, see the related [documentation](http://docs.gluonhq.com/javafxports/#_building_and_running).
This project will use Gluon VM, which is in Developer Preview. In case you run into issues, have a look at
[the status page](http://docs.gluonhq.com/gluonvm/)

## Clone the project

Clone or fork the project, and open it with your favorite IDE.

## Build and run the project

Either from the IDE or from command line the project can be clean, build and run with the following gradle tasks, assuming that you are running Java 9:

Clean and build:

    ./gradlew clean DeepLearningLinearClassifierApp:build

Run on desktop:

    ./gradlew DeepLearningLinearClassifierApp:run

Deploy to Android:

    ./gradlew DeepLearningLinearClassifierApp:androidInstall

Deploy to iOS simulator:

    ./gradlew  DeepLearningLinearClassifierApp:launchIPhoneSimulator
	
Deploy to iOS device:

    ./gradlew  DeepLearningLinearClassifierApp:launchIOSDevice
	
		
For more details about the different tasks or the `build.gradle` file, see the Gluon Mobile
[documentation](http://docs.gluonhq.com/charm/4.4.1/#_building_and_deploying).








