FROM anapsix/alpine-java
MAINTAINER David Charles Adetokunbo Gibbs
VOLUME /tmp
ADD ./target/continuous-engagement-0.1.0.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
