# Continuous Engagement

## Introduction
This application provides a simple engagement survey, supporting persistence of the results. It may be used and distributed under the terms of the GNU GPL, see "LICENSE".

## Implementation Overview
This is a Web Application using:
- BootStrap
- JQuery
- D3
- Java with Spring Boot
- MongoDB

The project is built with Maven.
As you have discovered the project repository is hosted on GitHub.
Travis is used to perform the Continuous Integration build and to push the Docker image to Docker Hub. See https://travis-ci.org/spidergawd/continuous-engagement .

The Docker image is available at https://hub.docker.com/r/spidergawd/continuous-engagement/ .

## Docker examples

The ```build.sh``` script performs a mvn build and builds the Docker image for the application.

The ```create-*``` scripts create the containers for the web application, the mongo database and the network. The application is exposed on ```localhost``` port 8080.

The ```start-*``` scripts start the named containers.

The ```mongo-client.sh``` script provides the mongo command line client.

## Developer Notes
The application is a self contained Spring Boot Application. If you want to run it without docker, run ```mongod``` and then run the application as in the script:

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
	DocumentRoot /home/user/work/continuousEngagement/application/src/main/resources/public
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
