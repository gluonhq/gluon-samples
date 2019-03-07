
CloudLink-Function-Mapper-Desktop
===========

Learn how you can retrieve external data in your JavaFX desktop application by configuring Remote Functions in Gluon CloudLink. 
The Java code calls a function with parameters, and this function makes a request to Gluon CloudLink. 
The Function Mapper in Gluon CloudLink will then call the corresponding external endpoint, providing the required parameters.

Prerequisites
-------------

* JDK 11
* `JAVA_HOME` environment variable pointing to JDK 11.
* A valid subscription to Gluon CloudLink. You can get it [here](http://gluonhq.com/products/cloudlink/buy/), and there is a 30-day free trial. Sign up and get a valid account on Gluon CloudLink and a link to download the Gluon CloudLink Dashboard. 

Instructions
------------

Install and open the Dashboard, and sign in using the Gluon account credentials provided above. You will need to create the remote functions used by this sample in the Dashboard's API Management view, as explained in the documentation linked below.

To execute the sample, do as follows:

```
mvn javafx:run
```