
Pivotal Network - Message Of The Day
===========

This is a sample application demonstrating how to extend an existing web application with a mobile application
 component. The web application is running on [Cloud Foundry](http://cloudfoundry.org) and uses
 [Gluon CloudLink](http://gluonhq.com/products/cloudlink), the
 [Spring Framework](http://spring.io) and [Spring Boot](http://projects.spring.io/spring-boot/).

## Application components

The sample is split up in two different components:

 * server: the web application, connecting with Gluon CloudLink
 * client: the mobile application

## Prerequisites

You need a valid subscription to Gluon CloudLink. You can get it [here](http://gluonhq.com/products/cloudlink/buy/), and 
there is a 30-day free trial. Sign up and get a valid account on Gluon CloudLink and a link to download the Gluon CloudLink 
Dashboard. 

Install and open the Dashboard, and sign in using the Gluon account credentials provided above. Go to the App Management view, and you will 
find a pair of key/secret tokens. Save the file `gluoncloudlink_config.json` under the `client` project 
`src/main/resources/` folder. The content of the file is a JSON object with the key and secret that will grant access
to Gluon CloudLink:

```json
{
  "gluonCredentials": {
    "applicationKey": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
    "applicationSecret": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
  }
}
```

Also, under the Credentials view, you'll find the `SERVER_KEY` required below.

### Running the server application locally

Create a system environment variable called `VCAP_SERVICES`:

```
export VCAP_SERVICES="{\"gluon-cloudlink-service-broker\":
[{\"credentials\":{\"hostname\":\"https://cloud.gluonhq.com\",\"serverKey\":\"YOUR_SERVER_KEY\"}}]}"
```

and run the server:

./gradlew :server:bootRun

### Running the webapp-mobile application on Cloud Foundry

TODO
#### Run the mobile application

To run the mobile application on your desktop, use the following command:

* Desktop
> Just run it from your IDE or from command line: `./gradlew client:run`
* Android
> Connect your Android device and run `./gradlew client:androidInstall`
* iOS
> Connect your iOS device and run `./gradlew client:launchIOSDevice`

Documentation
-------------

Read how to create this sample step by step [here](http://docs.gluonhq.com/samples/pivnetmotd/)

