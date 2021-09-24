#!/bin/sh

echo 'Deploying stateful geofincing faas applications on the dev (current) machine. \n
 Infrastructure will be brought up using docker(-compose) '

docker rm -f consul
docker rm -f kafka
docker rm -f grafana
docker rm -f zookeeper
docker rm -f prometheus
docker rm -f mongo

cd ./deployment/development-machine
docker-compose -f docker-compose-linux.yml rm
docker-compose -f docker-compose-linux.yml up -d
cd ../
sleep 10s

sh ./build-jars.sh

export SPRING_PROFILES_ACTIVE=local

cd ../location-update-publisher
echo "-------------------------------\n"
pwd
./gradlew bootRun
#
#cd ../location-aggregate/
#echo "-------------------------------\n"
pwd
#./gradlew bootRun
#
#cd ../realtime-fencing
#echo "-------------------------------\n"
pwd
#./gradlew bootRun
#
#cd ../bench-marking/
#echo "-------------------------------\n"
pwd
#./gradlew bootRun

cd ../
echo "-------------------------------\n"
pwd
