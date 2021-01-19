# Stateful Geofencing Faas
Master thesis progress report
##### The whole January 2021 has been dedicated to evaluation of system

### review
The deployed system looks like:
![Resulted deployed system](/work-report/images/Infrsutracture.png)

in which servers' resources is as below:
    - Server   , CPU CORES , RAM(GB)
    - server 1 , 8         , 16  
    - server 2 , 4         , 8  
    - server 3 , 4         , 8  
    - server 4 , 4         , 16

Server 4 is not part of architecture and only contains another instance of bench-marking application
. It will work parallel to the bench-marking instance deployed on server1 in order to produce more load on the system.
In most of the test scenarios, the resources available the bench-marking instances
was the bottleneck to throughput. As in stream processing systems, out put rate is a factor of input rate.


However, the total CPU and RAM available to our own application instances through Nomad cluster
is evaluated by Nomad as 16670 MHz and 16 GB respectively. In each test scenario, we change set a 
limit for the amount available to each instance of our applications using Nomad job config files.
In practice, some of these resources will be used by Kafka nodes most specifically which Nomad won't take into account. 

## Pure throughput
### Push leg
#### Description: stress test
Firstly we defined some fences for some movers.
Then we sent a socking stream of location-updates into the system.
We have graphs that show the total number of intersections happening in push leg of system.
We repeated this experiment while varying the shock size in order to realize the peak throughput
of system with current available resources.

#### Experiment 1
##### Deployment view
* Application,               #of instances,   RAM,      CPU
* location-update-publisher,      4,         500 GB,   500 Mhz
* location-aggregate,             3,        2500 GB,  2000 Mhz
* realtime-fencing,               4,         800 GB,  700 Mhz
* location-updates topic has replication factor of 3 and 12 partitions
#### Result 


![benchmarking(15,6)](/work-report/images/evaluation/ex1-benchmarking(15,6).png)

![benchmarking(19,7)](/work-report/images/evaluation/ex1-benchmarking(19,7).png)

![benchmarking(22,9)](/work-report/images/evaluation/ex1-benchmarking(22,9).png)

![benchmarking(23,10)](/work-report/images/evaluation/ex1-benchmarking(23,10).png)

#### Experiment 2
##### Deployment view
* Application,               #of instances,   RAM,      CPU
* location-update-publisher,      5,         700 GB,   400 Mhz
* location-aggregate,             3,        2600 GB,  2200 Mhz
* realtime-fencing,               5,         700 GB,   400 Mhz
* location-updates topic has replication factor of 3 and 12 partitions
#### Result
![benchmarking(24,10)](/work-report/images/evaluation/ex2-benchmarking(24,10).png)

So far the bottleneck is input rate which is limited by our physical available resources.
We can clearly see in the graphs that regardless of setup, push throughput (intersections/sec) follows pretty
much the exact parent of changes in input rate (location updates/sec).
So there is no point in continuing push throughput experiments with current available hardware.
However, it's worthy to mention that during one of the random experiments push throughput maxed at 21k/s.
![tuned-input-rate](/work-report/images/evaluation/1st-springboot-2.4.2-both-tuned-input-rate-UseZGC.png)

Comparing to [1], we have #TODO

----
### Poll leg
#### Description: stress test
Firstly we defined some fences and send some location updates for some movers.
Then we sent a socking load of query by fence request to the system.
We have graphs that show the total number of queries answered by poll leg of system.
We repeated this experiment while varying the load in order to realize the peak throughput
of system with current available resources.

#### Experiment 3
##### Deployment view
* Application,               #of instances,   RAM,      CPU
* location-update-publisher,      4,         500 GB,   500 Mhz
* location-aggregate,             2,        3200 GB,  2500 Mhz
* realtime-fencing,               4,         800 GB,   700 Mhz
* location-updates topic has replication factor of 3 and 12 partitions
#### Result 













