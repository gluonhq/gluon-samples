Whiteboard
==========

This is a sample application demonstrating how to extend an existing web application with a mobile application
 component. The web application is running on [Cloud Foundry](http://cloudfoundry.org) and uses
 [Gluon CloudLink](http://gluonhq.com/products/cloudlink), the
 [Spring Framework](http://spring.io) and [Spring Boot](http://projects.spring.io/spring-boot/).

It is loosely based on CloudFoundry's [Spring Music](https://github.com/cloudfoundry-samples/spring-music)
 sample application.

## Application components

The sample is split up in three different components:

 * webapp-base: the web application without any mobile extensions
 * webapp-mobile: the same web application, connecting with Gluon CloudLink
 * mobileapp: the mobile application

### Running the webapp-base application locally

The application can be started locally using the following command:

~~~
$ ./gradlew webapp-base:assemble
$ java -jar -Dspring.profiles.active=in-memory webapp-base/build/libs/webapp-base.jar
~~~

### Running the webapp-base application on Cloud Foundry

After installing the 'cf' [command-line interface for Cloud Foundry](http://docs.cloudfoundry.org/cf-cli/),
targeting a Cloud Foundry instance, and logging in, the application can be built and pushed using these commands:

~~~
$ ./gradlew webapp-base:assemble
$ cf push -f webapp-base/manifest.yml
~~~

The output will show the URL that has been assigned to the application.

### Running the webapp-mobile application on Cloud Foundry

#### Create a Gluon Application

Before running the mobile enabled version of the web application, you will need to create a Gluon Application
on Gluon CloudLink. From the PWS marketplace, choose the Gluon CloudLink service and select one of the available
plans. Once the service instance is created, navigate to the service details and click on the Manage link at the
top. This will redirect you to the Gluon CloudLink portal website.

From the Dashboard page, create a new Gluon Application by providing a name (i.e. Whiteboard) and clicking the Add
button. A new application will be created for you, together with a Key and a Secret.

This key and the secret need to be inserted into both the `webapp-mobile` and `mobileapp` components.

* In `webapp-mobile`, you can enter the key and secret in the class named `com.gluonhq.cloudlink.sample.whiteboard.mobile.service.CloudLinkService`.
The fields are defined as static fields at the top of the class.
* In `mobileapp`, the key and secret can be set in the class named `com.gluonhq.cloudlink.sample.whiteboard.service.Service`.
Again, the fields that need to be updated are defined at the top of the class as static fields.

#### Push the webapp-mobile application

To run the mobile enabled version of the web application, the commands for running the webapp-base application can
be re-used. Only the name of the directory needs to be changed:

~~~
$ ./gradlew webapp-mobile:assemble
$ cf push -f webapp-mobile/manifest.yml
~~~

#### Configure a Connector

When the mobile application generates an item on the whiteboard, Gluon CloudLink can push data to other back ends,
 by configuring a connector in your Gluon Application. Download the Gluon CloudLink Dashboard application. Run the
 application with the following command:

~~~
$ java -jar gluoncloud-apps-dashboard-1.0.0-all.jar
~~~

Sign in with the key and secret of your Gluon Application and navigate to the Configuration section. Select
`REST` from the combo box at the top and click the + button. In the URL textfield, enter the application URL
you received from PWS, appending it with `/cloudlink`. Click the Save button to update the configuration of
the REST connector.

#### Run the mobile application

To run the mobile application on your desktop, use the following command:

~~~
$ ./gradlew mobileapp:run
~~~

You can also attach a mobile device and run it on either Android or iOS:

~~~
$ ./gradlew mobileapp:androidInstall
~~~

~~~
$ ./gradlew mobileapp:launchIOSDevice
~~~