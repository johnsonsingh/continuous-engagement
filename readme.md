# Continuous Engagement

## Introduction
This application provides a simple engagement survey, supporting persistence of the results.

## Implementation
This is a Web Application using:
- JQuery
- D3
- BootStrap

The Server is implemented in Java using Spring Boot.
Results are persisted using MongoDB.

Travis is used to perform the Continuous Integration and to build and push the Docker image. See https://travis-ci.org/spidergawd/continuous-engagement .

The Docker image is available at https://hub.docker.com/r/spidergawd/continuous-engagement/ .

## Developer Notes
The application is a self contained Spring Boot Application. If you want to run this without docker and you have ```mongod``` running you can run as in the following script:
~~~~
./buildAndRun.sh
~~~~

It's quite inconvenient to need to rebuild and restart the server to test changes to html and javascript assets which are packaged in the jar. As a workaround Apache can be used to serve the content from the devlopment directory location. This requires setting some Apache config options and setting of CORS headers by the application. The application is configured to allow requests from ```localhost```.

For development purposes only, a simple example for configuring Apache to serve the content from the development directory follows:
- update "/etc/apache2/apache2.conf" ```<Directory/>``` directive to ```Require all granted```
~~~
<Directory />
	Options FollowSymLinks
	AllowOverride None
    #Require all denied
	Require all granted
</Directory>
~~~
- update "/etc/apache2/sites-enabled/000-default.conf" to serve content from the development directory location, for example:
~~~
<VirtualHost *:80>
	DocumentRoot /home/david/work/continuousEngagement/application/src/main/resources/public
	ErrorLog ${APACHE_LOG_DIR}/error.log
	CustomLog ${APACHE_LOG_DIR}/access.log combined
</VirtualHost>
~~~
- restart Apache
~~~
sudo apachectl -k graceful
~~~
## RoadMap
- Provide LDAP integration and implicitly include group or department if possible
- Implement one way hash of "user" for anonymity
- Average user inputs per day to allow revision of submitted survey
- Implement simple desktop "pop-up" application to encourage daily inputs
- Implement more reports
