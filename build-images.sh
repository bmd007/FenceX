#!/bin/sh

echo 'Building docker images out of involved applications in stateful geofincing faas'

cd common
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal

cd ../function/
echo "-------------------------------\n"
pwd
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal


cd ../bench-marking/
echo "-------------------------------\n"
pwd
./gradlew clean bootBuildImage

cd ../location-aggregate/
echo "-------------------------------\n"
pwd
./gradlew clean bootBuildImage

cd ../location-update-publisher
echo "-------------------------------\n"
pwd
./gradlew clean bootBuildImage

cd ../realtime-fencing
echo "-------------------------------\n"
pwd
./gradlew clean bootBuildImage

cd ../
echo "-------------------------------\n"
pwd
