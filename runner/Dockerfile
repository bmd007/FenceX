FROM adoptopenjdk/openjdk12

MAINTAINER "Mohammad Mahdi Amini 'bmd579@gmail.com'"

ADD build/libs/stateful-geofencing-faas.jar /stateful-geofencing-faas.jar

ENV JAVA_OPTS ""

ENTRYPOINT ["sh", "-c", "exec java -jar $JAVA_OPTS /stateful-geofencing-faas.jar"]

EXPOSE 8242
EXPOSE 8243