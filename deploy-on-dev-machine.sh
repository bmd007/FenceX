#!/bin/sh

echo 'Deploying stateful geofincing faas applications on the dev (current) machine. \n
 Infrastructure will be brought up using docker(-compose) '

docker rm -f consul
docker rm -f kafka
docker rm -f grafana
docker rm -f zookeeper
docker rm -f prometheus

cd ./deployment/development-machine
docker-compose -f docker-compose-linux.yml rm
docker-compose -f docker-compose-linux.yml up -d
cd ../
sleep 10s

SPRING_PROFILES_ACTIVE=local

cd ../location-update-publisher
pwd
./gradlew clean bootRun

cd ../location-aggregate/
pwd
./gradlew clean bootRun

cd ../realtime-fencing
pwd
./gradlew clean bootRun

cd ../bench-marking/
pwd
./gradlew clean bootRun

cd ../
pwd
