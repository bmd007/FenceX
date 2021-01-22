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
    - Application              , #of instances,     RAM       ,      CPU
    - location-update-publisher,      4       ,         500 GB,   500 Mhz
    - location-aggregate       ,      3       ,        2500 GB,  2000 Mhz
    - realtime-fencing         ,      4       ,         800 GB,  700 Mhz
    - location-updates topic has replication factor of 3 and 12 partitions
#### Result 


![push-benchmarking(15,6)](/work-report/images/evaluation/ex1-benchmarking(15,6).png)

![push-benchmarking(19,7)](/work-report/images/evaluation/ex1-benchmarking(19,7).png)

![push-benchmarking(22,9)](/work-report/images/evaluation/ex1-benchmarking(22,9).png)

![push-benchmarking(23,10)](/work-report/images/evaluation/ex1-benchmarking(23,10).png)

#### Experiment 2
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       5       ,   700 GB ,   400 Mhz
     - location-aggregate       ,       3       ,   2600 GB,  2200 Mhz
     - realtime-fencing         ,       5       ,   700 GB ,   400 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking(24,10)](/work-report/images/evaluation/ex2-benchmarking(24,10).png)

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
     - Application              , #of instances,            RAM,      CPU
     - location-update-publisher,      4       ,         500 GB,   500 Mhz
     - location-aggregate       ,      2       ,        3200 GB,  2500 Mhz
     - realtime-fencing         ,      4       ,         800 GB,   700 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result 
![poll-benchmarking(13,7)](/work-report/images/evaluation/ex3-benchmarking(13,7).png)

![poll-benchmarking(16,9)](/work-report/images/evaluation/ex3-benchmarking(16,9).png)

#### Experiment 4
##### Deployment view
     - Application              , #of instances,     RAM  ,      CPU
     - location-update-publisher,      4       ,    500 GB,   500 Mhz
     - location-aggregate       ,      2       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,      4            800 GB,   700 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result 
![poll-benchmarking(19,10)](/work-report/images/evaluation/ex4-benchmarking(19,10).png)

![poll-benchmarking(22,10)](/work-report/images/evaluation/ex4-benchmarking(22,10).png)

Although the CPU usage on location-aggregate nodes peaked to 100% during these experiments, 
again the bottleneck was input rate which is limited by our physical available resources.
We can clearly see in the graphs that regardless of setup, poll throughput (queries/sec) follows pretty
much the exact parent of changes in input rate (queries sent/sec).
So there is no point in continuing poll throughput experiments with current available hardware.

Comparing to [2], we have out performed in terms of poll throughput #TODO




## Availability
### Push leg
#### Description: 
Firstly we defined some fences for some movers.
Then we start an ongoing stream of location-updates into the system.
We have graphs that show the total number of intersections happening in push leg of system.
Now we restart one of the instances of realtime-fencing. Throughput should decrease temporarily due
to re-balancing (of kafka consumers against topic partitions). 
Eventually when the started instance is up and running again, throughput goes back to normal.
If instances of realtime-fencing have enough resources, there might not be any decrease in throughput.
#### Experiment 5
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       5       ,   700 GB ,   400 Mhz
     - location-aggregate       ,       2       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       6       ,   800 GB ,   400 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result 
![push-benchmarking-ongoing-2per7sec](/work-report/images/evaluation/ex5-benchmarking-ongoing-2per7sec.png)

![push-benchmarking-ongoing-6sec](/work-report/images/evaluation/ex5-benchmarking-ongoing-2per6sec.png)

As you can see in the graph, there are two points in the timeline at which number of realtime-fencing
instances goes down (by 1 and 2) and later comes back to 6 again. During the time that
takes for all 6 instances to be up and running again, throughput slightly drops and eventually recovers.
Since we are using kafka topics as durable storage of location updates, the location updates which
didn't get a chance to get processed, get it after re-balancing. As a result, we have event
higher throughput than input rate temporarily after re-balancing finishes.


### Poll leg
#### Description: 
Firstly we defined some fences and send some location reports for some movers.
Then we start an ongoing load of queries to the system.
We have graphs that show the total number of queries answered in poll leg of system.
Now we restart one of the instances of location-aggregate. Throughput should decrease temporarily due
to re-balancing (of kafka consumers against topic partitions). 
Eventually when the started instance is up and running again, throughput goes back to normal.
If instances of location-aggregate have enough resources, there might not be any decrease in throughput.
#### Experiment 6
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       5       ,   700 GB ,   400 Mhz
     - location-aggregate       ,       2       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       6       ,   800 GB ,   400 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result 
![poll-benchmarking-ongoing-2per10sec](/work-report/images/evaluation/ex6-benchmarking-ongoing-2per10sec.png)
In this system we are using poll based health checks which means instead of each application every now and agains
reports its status to Consul (service registry), Consul asks services about theirs status every now and again.
(Initially it's up to Nomad to tell about instances to Consul).
So when we restart an instance of location-aggregate, consul won't get informed about it
soon enough and keeps giving IP of the restarted instance to bench-marking application.
Which leads to queries reaching the instance when it's state it under preparation and
resulting errors avoids a successful restart.

One way to solve this in production is to use BLUE/GREEN deployment strategy. This approach is
very important for services with large in memory data sets like location-aggregate.
Implementing special health checks for KafkaStreams is also another option.


#### Experiment 7
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       0       ,   700 GB ,   400 Mhz
     - location-aggregate       ,       4       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       0       ,   800 GB ,   400 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result 
![poll-benchmarking-ongoing-2per10sec](/work-report/images/evaluation/ex7-benchmarking-ongoing-2per10sec.png)
Deploying more instances of location-aggregate helped with zeroing the query load
during preparation phase after re-balancing. As a result, the restarted instance managed to 
get back to work successfully. Eventually throughput induced back to its pre restart value.






## Strong scalability
### Push leg
#### Description:
Firstly we defined some fences for some movers.
Then we start an ongoing stream (fixed rate) of location-updates into the system. 
We start with deploying only one resourceful instance of realtime-fencing. 
However, this instance should not be too rich. We hope for this instance to be overwhelmed.  
Then we repeat the experiment with 2 such instances, and the throughout should increase.
We continue adding such instances and repeat the experiment until adding more instances 
won't increase throughout.
#### Experiment 8
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       1       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-2*4sec](/work-report/images/evaluation/ex8-benchmarking-ongoing-2per4sec.png)

#### Experiment 9
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       2       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-2*4sec](/work-report/images/evaluation/ex9-benchmarking-ongoing-2per4sec.png)

#### Experiment 10
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       3       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-2*4sec](/work-report/images/evaluation/ex10-benchmarking-ongoing-2per4sec.png)

#### Experiment 11
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       4       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-2*4sec](/work-report/images/evaluation/ex11-benchmarking-ongoing-2per4sec.png)

### Repeat
Now we repeat experiments 8 to 11 slightly different. 
Differences are:
   * increased input rate
   * after observing throughput with x number of instances, we kill the input rate,
so that the buffered location updates get processed. When throughput reaches zero, we increase the number of 
instances and continue. It will make comparison of input rate with throughput more meaningful.
   * We start with 3 instances of realtime-fencing (instead of 1)

#### Experiment 12
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       3       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-3*4sec](/work-report/images/evaluation/ex12-benchmarking-ongoing-3per4sec.png)


#### Experiment 13
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       4       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-3*4sec](/work-report/images/evaluation/ex13-benchmarking-ongoing-3per4sec.png)


#### Experiment 14  
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       5       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-3*4sec](/work-report/images/evaluation/ex14-benchmarking-ongoing-3per4sec.png)
failure to keep the input rate high while having 5 instances. why?

#### Experiment 15
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       6       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-3*4sec](/work-report/images/evaluation/ex15-benchmarking-ongoing-3per4sec.png)
failure to keep up with input rate with 6 instances. why? The input rate pressure is low enough for instances to 
try to process them without much of buffering while it's high enough to overwhelm the intances CPU 100% (no further 
progress possible).

#### Experiment 16
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       4       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       0       ,   2700 GB,  2700 Mhz
     - realtime-fencing         ,       12       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![push-benchmarking-ongoing-3*4sec](/work-report/images/evaluation/ex16-benchmarking-ongoing-3per4sec.png)
Same as previous experiment. The moment input rate goes above 15k/s, the throughput falls down below 10k/s. 
Event with 12 instances of realtime-fencing.

We have repeated this experiment but gave each instance 90Mhz of CPU (instead of 30). Result was promising. But,
our hardware can't keep the input rate high enough. As usual the bottleneck is input rate.
![push-benchmarking-ongoing-3*4sec](/work-report/images/evaluation/90MHz-ex16-benchmarking-ongoing-3per4sec.png)


## Strong scalability
### Poll leg
#### Description:
Firstly we a load of location updates for some movers.
Then we start an ongoing stream (fixed rate) of queries (by fence) to the system.
We start with deploying only one resourceful instance of location-aggregate.
However, this instance should not be too rich. We hope for this instance to be overwhelmed.  
Then we repeat the experiment with 2 such instances, and the throughout should increase.
We continue adding such instances and repeat the experiment until adding more instances
won't increase throughout.
#### Experiment 17
##### Deployment view
     - Application              ,  #of instances,   RAM    ,      CPU
     - location-update-publisher,       0       ,   700 GB ,   200 Mhz
     - location-aggregate       ,       1       ,   1500 GB,  100 Mhz
     - realtime-fencing         ,       0       ,   500 GB ,   30 Mhz
     - location-updates topic has replication factor of 3 and 12 partitions
#### Result
![poll-benchmarking-ongoing-2*4sec](/work-report/images/evaluation/ex17-benchmarking-ongoing-2per4sec.png)

