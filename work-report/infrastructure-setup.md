# Stateful Geofencing Faas
 Master thesis progress report 
##### The whole December 2020 has been dedicated to infrastructure setup and improvement

We used two container orchestration tools named Docker compose and
nomad.
Docker compose is used for easily deploying tools like kafka cluster, consul cluster
, mongodb. This is not a production friendly approach as in production environments
each of the kafka cluster nodes, for example, are installed directly on a separate powerful machine.

Nomad on the other hand is a production level container orchestration tool
which we used to deploy the applications implemented during this thesis.

The deployed system at the moment looks like:
![Resulted deployed system](/work-report/Infrsutracture.png)

### Challenges and lessons:
It was not very straight forward how to setup the networking between
Docker compose and Nomad in a way that all the tools and services can access each other.
Each Docker compose unit can have its own isolated network.
Each Docker image deployed by nomad can have its own isolated network as well.
We also have a private network between our virtual machines.
We ended up configuring all the docker, docker compose and nomad networks to
"host" mode with mean no internal isolated networks. So all the involved processes are
part of the private network between virtual machines. However, dynamic port
allocation is in action to allow deploying multiple instances of same application on 
essentially one virtual machine.
