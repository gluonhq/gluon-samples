InAppBilling Sample
=======

A JavaFX Application that shows how to use the [in-app-billing service](https://gluonhq.com/products/mobile/charm-down/) 
to manage in-app products and handle product purchases for your mobile application, on Android and iOS.

Instructions
------------
To execute the sample, do as follows:

* Desktop
> Just run it from your IDE or from command line: `./gradlew run`
* Android
> Connect your Android device and run `./gradlew androidInstall`
* iOS
> Connect your iOS device and run `./gradlew launchIOSDevice`

Requirements
-------------

For Android (Google Play Developer Console) and iOS (Apple App Store Connect), the list of in-app billing products has
 to be registered, and a selling price assigned:

 - com.gluonhq.inappbilling.health_potion (consumable product)
 - com.gluonhq.inappbilling.wooden_shield (non consumable product)

In order to test the in-app billing services the application has to be signed for release (Android) or distribution (iOS).

*Android*

A valid Android key is required. From the Google Play developer account copy the base64-encoded RSA public key.

*iOS*

A valid provisioning profile for distribution that allows in-app purchase services is required.