#!/bin/sh

echo 'Building jar artifacts out of involved applications in stateful geofincing faas'

cd common
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal

cd ../function/
echo "-------------------------------\n"
pwd
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal

cd ../bench-marking/
echo "-------------------------------\n"
pwd
./gradlew clean jar

cd ../location-aggregate/
echo "-------------------------------\n"
pwd
./gradlew clean jar

cd ../location-update-publisher
echo "-------------------------------\n"
pwd
./gradlew clean jar

cd ../realtime-fencing
echo "-------------------------------\n"
pwd
./gradlew clean jar

cd ../
echo "-------------------------------\n"
pwd
