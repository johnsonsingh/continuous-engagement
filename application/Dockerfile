FROM anapsix/alpine-java
MAINTAINER David Charles Adetokunbo Gibbs
VOLUME /tmp
ADD ./target/continuous-engagement-0.1.0.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=live -Dspring.data.mongodb.uri=mongodb://127.0.0.1:32770/ContinuousIntegration","-jar","/app.jar"]
