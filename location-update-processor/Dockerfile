FROM adoptopenjdk/openjdk14

MAINTAINER "Mohammad Mahdi Amini 'bmd579@gmail.com'"

ADD build/libs/statefull-geofencing-faas-location-update-processor-beta.jar /application.jar

ENV JAVA_OPTS ""

ENTRYPOINT ["sh", "-c", "exec java -jar $JAVA_OPTS /application.jar"]

EXPOSE 9585
EXPOSE 9586
EXPOSE 8084
EXPOSE 9097
