#!/bin/sh

echo 'Deploying stateful geofincing faas applications on the dev (current) machine. \n
 Infrastructure will be brought up using docker(-compose) '

pwd
docker rm -f consul
docker rm -f kafka
docker rm -f grafana
docker rm -f zookeeper
docker rm -f prometheus
docker rm -f mongo
echo "-------------------------------\n"

cd ./deployment/development-machine
pwd
docker-compose -f docker-compose-linux.yml rm
docker-compose -f docker-compose-linux.yml up -d
cd ../../
echo "-------------------------------\n"

echo $1
if [ $1 = 'clean' ];
then
pwd
sh ./build-jars.sh
echo "-------------------------------\n"
fi

export SPRING_PROFILES_ACTIVE=local

cd location-update-publisher
pwd
./gradlew bootRun &
cd ../
echo "-------------------------------\n"

cd location-aggregate/
pwd
./gradlew bootRun &
cd ../
echo "-------------------------------\n"

cd realtime-fencing
pwd
./gradlew bootRun &
cd ../
echo "-------------------------------\n"


cd bench-marking/
pwd
./gradlew bootRun &
cd ../
echo "-------------------------------\n"

