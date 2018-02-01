
CommentsJS
==========

 A JavaScript application using [Gluon CloudLink](https://gluonhq.com/products/cloudlink)
 and [NativeScript](https://www.nativescript.org):

 - Gluon CloudLink: a framework for connecting your enterprise to your mobile users
 - NativeScript: a framework for building native mobile apps with JavaScript

Prerequisites
-------------

#### Gluon CloudLink

You need a valid subscription to Gluon CloudLink. Choose from one of the available tiers
at the [CloudLink subscription page](http://gluonhq.com/products/cloudlink/buy/) to create
a Gluon CloudLink account. All tiers come with a 30-day free trial period.

When you've registered successfully, you can sign in to [Gluon Dashboard](https://gluon.io) by
using the credentials that you provided during registration. Navigate to the Credentials section
where you will find your client application credentials. Save the file `gluoncloudlink_config.json`
in the project's `app/` folder. The content of the file is a JSON object with a key and secret
that will grant access to Gluon CloudLink:

```json
{
  "gluonCredentials": {
    "applicationKey": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
    "applicationSecret": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
  }
}
```

#### NativeScript

Make sure you followed the instructions to install NativeScript on your computer. More information can be found
at their website: https://www.nativescript.org/

Instructions
-----------------
To execute the sample, do as follows:

* Android
> tns run android
* iOS
> tns run ios
