#!/bin/sh

echo 'Building docker images out of involved applications in stateful geofincing faas'

cd common
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal

cd ../function/
pwd
./gradlew clean thinJar publishMavenJavaPublicationToMavenLocal publishToMavenLocal


cd ../bench-marking/
pwd
./gradlew clean bootBuildImage

cd ../location-aggregate/
pwd
./gradlew clean bootBuildImage

cd ../location-update-publisher
pwd
./gradlew clean bootBuildImage

cd ../realtime-fencing
pwd
./gradlew clean bootBuildImage

cd ../
pwd
