
CloudLinkRestConnector
===========

A simple web application that can be used as a [REST Connector](http://docs.gluonhq.com/cloudlink/connectors.html#_rest_connector)
in Gluon CloudLink. This application can be used together with the [Notes sample](https://bitbucket.org/gluon-oss/samples/src/master/Notes)
if you make a small adjustment to the `com.gluonhq.notesapp.service.Service.java` file. Replace the entire code in the
`postConstruct()` method with the following:

```
    gluonClient = GluonClientBuilder.create()
            .credentials(new GluonCredentials(YOUR_APPLICATION_KEY, YOUR_APPLICATION_SECRET))
            .operationMode(OperationMode.CLOUD_FIRST)
            .build();
```

Instructions
------------
To execute the sample, you will need to configure a database. The settings for the database name, the hostname,
the username and password can be found/configured in the file `src/main/resources/sun-resources.xml`. That is a
configuration file that can be used directly in the [GlassFish](https://glassfish.java.net/) application server.
If you are using a different application server, configure it accordingly so that it can find the JDBC resource
that is defined in the `src/main/resources/META-INF/persistence.xml` file.

Build the war by running the gradle `war` task and deploy the generated war file in your application server. Next,
use the Gluon CloudLink Dashboard to configure your application with a REST Connector. The URL that you need to enter
might look like this: `http://HOSTNAME:PORT/CONTEXTROOT/rest` where you replace the host name, port number and context
root accordingly. Make sure that the host is publicly accessible for Gluon CloudLink.

For example, you can deploy the war in GlassFish with the following command:

`asadmin deploy --name CloudLinkRestConnector --contextroot cloudlinkrest build/libs/CloudLinkRestConnector.war`

Documentation
-------------

Read more about Connectors in Gluon CloudLink on our [documentation website](http://docs.gluonhq.com/cloudlink/connectors.html).
