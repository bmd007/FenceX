#!/bin/sh

echo 'Building jar artifacts out of involved applications in stateful geofincing faas'

cd common
pwd
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal
cd ../
echo "-------------------------------\n"

cd function/
pwd
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal
cd ../
echo "-------------------------------\n"

cd bench-marking/
pwd
./gradlew clean jar
cd ../
echo "-------------------------------\n"

cd location-aggregate/
pwd
./gradlew clean jar
cd ../
echo "-------------------------------\n"

cd location-update-publisher
pwd
./gradlew clean jar
echo "-------------------------------\n"

cd ../
cd realtime-fencing
pwd
./gradlew clean jar
cd ../

echo "-------------Jar building is done------------------\n"
pwd
