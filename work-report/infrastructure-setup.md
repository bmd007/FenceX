# Stateful Geofencing Faas
 Master thesis progress report 
##### The whole December 2020 has been dedicated to infrastructure setup and improvement

We used two container orchestration tools named Docker-compose and
Nomad.
Docker-compose is used for easily deploying tools like kafka (cluster), consul (cluster)
, mongodb. This is not a production friendly approach as in production environments
each of the kafka cluster nodes, for example, are installed directly on an independent powerful machine.

Nomad on the other hand is a production level container orchestration tool
which is used to deploy the applications implemented during this thesis.

The deployed system at the moment looks like:
![Resulted deployed system](/work-report/images/Infrsutracture.png)

### how
It was not very straight forward how to set up the networking between
applications deployed in Docker-compose and, the ones deployed in Nomad. 
All of those applications and tools need to be able to access each other.

By default, each Docker-compose unit has its own isolated network, as well as each 
Docker container deployed by/in Nomad. There is a private network also between virtual machines.
We ended up configuring all the Docker, Docker-compose and Nomad networks to
"host" mode with mean no internal isolated networks. So all the involved processes are
part of the private network between virtual machines. However, thanks to dynamic port
allocation it becomes possible to deploy multiple instances of same application on 
essentially one virtual machine.










