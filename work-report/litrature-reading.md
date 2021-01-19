# Stateful Geofencing Faas
 Master thesis progress report 
##### November 16th to November 30th has been dedicated to literature reading
which was helpful to understand the similar works, metrics for 
evaluating the result and getting ideas for advancement/improvement

## Summary of reviewed materials

### Geofencing
Intersecting a geometrical shape with a set of positions so that 
you can find out what points are in the specified geometry.
###### use case examples: 
 * finding the closest taxi(s) to a traveler
 * alarming care takers about care givers entering/exiting safe zones
 * disaster management (finding the closest fire fighters ) 
 * marketing (sending targeted ads for people close by to store)
 * addiction control
##### poll vs push style
In poll style of geofencing, fences are highly dynamic. So when 
there is a fence, we use that fence to query a data base of points.

In push style of geofencing, fences are less changing. Once a new position 
update arrives for a mover, new position gets checked against being inside a
previously defined fence.
 
## Large Scale Indexing of Geofences, 2014
https://ieeexplore.ieee.org/abstract/document/6910110

In this article, a poll style geofencing system is introduced that supports large scales
of data and load. Dynamic caching and work load distribution over multiple instances
of workers are the base of achieved scalability. Each worker instance is
responsible for indexing a region of the world, which allows for lower query and index latency.
Each worker instance has only one thread which removed the need for thread safe data structures and synchronization solutions.
Also, each worker instance takes care of areas around it's dedicated region as a solution for border problems.

## Using Complex Event Processing for implementing a geofencing service, 2013
https://ieeexplore.ieee.org/abstract/document/6662608

This article illustrates a push style geofencing system that relies on a CEP (complex event processing) 
for fence entrance/exit detection. This CEP is also used for detecting other patterns like 
*user left the fence and returned within 1 minute*. 
The architecture of this system apart from using a CEP engine, is not more significant than 3 tiers (layered).
The authors evaluations resulted in the system handling more than 20,000 event/sec with 1000 queries active in a single CEP instance.
The unit of concurrency of the system was not clear in the article. So it's not clear what are the bounds for scalability of the introduced system.
 
What CEPs can do with events is pretty much similar to functional programming operations like:
 filtering, translation, splitting, aggregation, composition
 
## Stream processing with apache flink fundamentals (chapter1) 
book, 2017, Fabian Hueske and Vasiliki Kalavri

__Data flow graph__ is a useful tool to express and study how data is flown in a stream processing system.
There are two type of data flow graph: logical and physical. 
In a logical data flow graph nodes represent `operators` like sum, count, filter, aggregate and ... .
It doesn't tell anything about deployment.
On the other hand, a physical data flow graph illustrates how exactly the deployed instances look in one image.
Like how many instances of each `task` will be deployed. Not to forget that nodes are called `task` in the physical data flow graph.
     

![Physical data flow](/work-report/images/physical-data-flow-graph.png)


__Parallelism__ has two dimensions of `task` and `data` in stream processing. 
 * task: Tasks from different operators process same data.
 * data: Tasks from same operator process different data (partitioning). Same data ends up in same task.
 
 __Latency__ is defined as the time takes for ONE event to be processed. In an ideal stream processing system, the latency reflects
 the actual processing work on the events which means events don't wait to be processed. Events get processed as soon as they arrive.
 `95th-percentile latency value of 10ms` is an example of expressing latency of system. It means 95 percent of events are processed within 10 ms.
 
 __Throughput__ is dependent on both latency and input rate. So low throughput doesn't necessarily mean high latency. 
 A better metric is `peak throughput` which shows the limit on the performance of system at maximum load. When system reaches 
 peak throughput, increasing input load, will reduce throughput and can even lead to data loss (due to buffer over flow).
 
 __Operations__ are logical representation of tasks. They process ONE event at a time. `Stateless` operations like filter and map
 are not concerned with past. They are easier to scale and more resilient. While `stateful` operations keep a snapshot of past (state).
 The state may mutate after processing each event. operations like sum, count, average, ... are stateful. Such operations are harder to scale.
 For example partitioning geospatial data is not easy and partitioning policy may differ based on the use case. 
 
 
 ## I ♥ Logs (chapter 1)
   book, 2015, Jay Kreps

__State__ is a value for something like balance of bank account. And state has a value like 5 SEK.

__Event__ s represent what has happened like a bank account transaction. I have paid 3 SEK. 
Events can be defined in slightly different ways as well. Like latest snapshot of a state. Or a command with expected side effect.
Examples respectively are `my latest balance is 5 SEK` and `dear bank, pay this bill using my account`.

__Table__ is a data/state holder (usually in shape of key:value/map) that contains the latest/updated states. 
An example of table is collection of customers' accounts (balances) in a bank application.

Assuming we have an initial value for a state, we can rebuild any state by processing the events which built that state at first palce.
We can also generate those events as we change the state during the natural life cycle of application. 
So we can use events to (re)build a table of states. We can also express changes of table as a stream of events. 
This is called __Duality of state and event__. 

##### Examples:
* Initial state of bank account balance = 0
* Command (event): Deposit 5 SEK
* Event: Deposited 5 SEK
* Resulted state: balance = 5
* Change log (event): Balance increased from 0 to 5 SEK
* State Update Style event: Latest balance is 5 SEK
Please note that commands are meaningful only in certain architectural patterns like CQRS (Command and Query Responsibility Segregation). 

__Log__ (commit log) is like a queue or journal, a sequence of records ordered by time. It can be used to keep track of
 events (what has happened) in addition to when they happened (relative to each other). Logs can be used to replicate of data over 
 instances of database, different systems, different indexes and ... without facing inconsistency (eventual-occasional consistency). 
 Without a log, an orchestrator should be in place that implements distributed transactions (double writes).
 Which is proven to be practically impossible to do right. With commit log, master changes it's own state and then publishes 
 those changes as logs. Slaves subscribe to them and by processing ordered logs eventually become consistent with master.
 However, replication can be achieved using logs without masters.
 When there is no master, all instances of an application subscribe to the log of events and process them until eventually reach same state.
The approach involving master and slaves is called `primary backup` and the other one is `state machine replication`. 
Both of them require deterministic subscribers.


## On the FaaS Track: Building Stateful Distributed Applications with Serverless Architectures,2019
https://dl.acm.org/doi/10.1145/3361525.3361535

This article introduces a serverless stateful function as a service platform. 
There has been successful Faas platforms like AWS lambda and Google cloud Function already out there. However, they don't
support stateful operations in a low latency and flexible manner. 
They achieved stateful Faas using a global in memory strongly consistent data layer for state mutation. 
This data layer can be accessed by all the functions (parallel workers/cloud threads).
For work load distribution, they partitioned the data layer using hashing.
For durability, they allow replication of data in the data layer. By default, no data is replicated (replication factor is 1).
Fault tolerance comes from data replication and possibility of making functions execute again when facing failure (retry).
Apparently this platform is best suited for computations that require direct state mutations
 (rather than immutable state). 
 
## glue them all together
In this thesis we are going to build a highly scalable low latency high throughput geofencing system that supports both push and
poll styles. In order to achieve those properties, we will use an in memory (to remove IO latency) data base.
The system will include a operator that run the in memory data base and that operator is scaled out into multiple tasks (instances).
All the tasks will have same similar view of world (state). To achieve such replication we use kafka topics
as a commit log with kafka streams on top.
Kafka Streams is a stream processing framework (library) that apart from making occasional/eventual consistent data replication
possible, allows for complex stream processing. Stream processing helps us to support push based geofencing in a 
highly scalable manner. Kafka Streams allows defining processors over stream of events, tables as aggregates of a stream of events,
joining such tables and streams and so on. 

Also, kafka topics are partitioned which allows for word load sharing (data parallelism). 
Each partition of a kafka topic can be replicated over different instances of kafka which brings fault tolerance.