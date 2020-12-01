# Stateful Geofencing Faas
 Master thesis progress report 
##### December 1st to December 7th has been dedicated to coming up with a scientific hypothesis for the initial idea

### Geofencing (recap)
Intersecting a geometrical shape with a set of positions so that 
you can find out what points are in the specified geometry.
###### use case examples: 
 * finding the closest taxi(s) to a traveler
 * alarming care takers about care givers entering/exiting safe zones
 * disaster management (finding the closest fire fighters ) 
 * marketing (sending targeted ads for people close by to store)
 * addiction control
##### poll vs push style
 * In poll style of geofencing, fences are highly dynamic. So when there is a fence, 
we use that fence to query a data base of points.
 * In push style of geofencing, fences are less changing. Once a new position 
update arrives for a mover, new position gets checked against being inside a previously defined fence.
 
### Our suggested system with a little back ground
In this thesis we design a geofencing system that supports both poll and push styles of geofencing.
For convenience from now on we call our system FenceX.
FenceX is designed based on microservices[] and stream processing principles[]. It also conforms to reactive manifesto[].
As a consequence FenceX has an elastic, responsive and resilient nature 
that handles with high scales of load very well while keeping the operational latency low. 

Since we are mixing stream processing and microservices together, the modules in the FenseX architecture
can be called both micro-services and operations (logically speaking). When we talk in more physical terms, they 
are called instances and tasks respectively. 
So from now on, we will use words `operation, microservice, services, event processor, 
processor, subscriber, publisher and subsystems` to the express pretty much same thing. 
Same goes for instance and task. Unless we are talking explicitly about a microservice that is not involved in 
the stream processing part of the system. 

The key component in our architecture is an event streaming tool called Kafka[]. Kafka is a highly available
distributed implementation of commit-log/journal[]. Using Kafka in a distributed architecture means
applying publish-subscribe pattern for inter service communication (asynchronous). 
A reactive system relays on asynchronous messaging to achieve loosely coupled isolated sub systems.
Kafka gives the asynchronous messaging touch of reactive ness to FenseX.

In order to use Kafka we need to define topics (log). They can have multiple publishers and multiple subscribers.
Each Kafka topic can be divided into many partitions and subscribers will get events only from a subset of those 
partitions. So we can deploy multiple instances of an event processor/subscriber and distribute the processing 
load between them. This is called data parallelism. 
So in other words, we get out of the box load balancing using Kafka (topic partitions).
It is responsibility of Kafka client library to put/read events into/from different partitions.

Kafka topics have a replication factor that events from each topic will get replicated into different kafka nodes
accordingly. Such event replication helps with availability and resiliency of FenseX.

We also have different operations subscribing to the same topic (task parallelism). Those operators are
loosely coupled by sharing as little as possible (share nothing principle[]).
They achieve it by having their own databases. Such isolation of operations and data, makes the system 
more resilient. Because local errors in one operation do not bring the whole system down.


#### Logical data flow diagram
The picture below illustrates operators of FenseX. 

![Logical data flow](/work-report/logical%20data%20flow%20diagram.png)

__Location update publisher__ somehow gets reports from movers when
they change their position.  Then publishes a `mover location update` event. 

__Filter__ as the name suggests, only allows certain (valid) locations updates to enter the system.

__Location aggregate__ is the poll leg of FenseX. It is a stateful operation that keeps the latest reported location
of each mover in its state store. This operator exposes HTTP apis for querying those locations against a mover id or a 
fence. The database behind this processor's state store can index geospatial data.

__Real time fencing__ is a part of push leg of FenseX. It is a stateful operation keeping track of fences for movers.
Its internal architecture is `event-sourcing` and exposes HTTP apis for CRUD operations on fences. 
The table (state) in this operator is joined with the stream of location updates (output of filter) so that when
ever a mover moves, we can tell if it is inside or outside a fence. Movers should have their own fence predefined. 
In fact when ever a new location update arrives to this operator, a join function will be executed.
That function has two inputs: `the location update` and the `fence` defined for the mover.
In this function, we intersect the new location coordinates with the predefined fence to check if the mover is in
the fence or not. This is computed using a geo library so no geo index or geo query is involved in the push leg.


### distributed system challenges and pros
One of the main sources of latency is IO. It can be network IO or disk IO. Consider, as an example, querying a 
database record over network that has its records persisted on disk. Or triggering an update that makes a change
in the indexes in that database. The solution to such latencies is using an in-memory embedded(co-located) database.
We use H2 as the database behind the poll leg of FenseX. H2 allows indexing/querying geospatial data.
Not only this database is not deployed separately from operations but also it's part of their class path as a library.
So no network IO and no disk IO is involved. It means that each of the instances of poll leg FenseX 
have their own embedded in-memory H2. 

Operators read events from a topic, process it and change their state (if they are stateful).
Since their deployed tasks are isolated and decoupled and read events from non similar partitions, their database
will have different data. So in our case each of the __location aggregate__ instances should have 
a different portion of location updates. Consequently, when querying different instances, 
we will get different results. Which means inconsistency. 
What if we are querying the database against a fence the involved positions persisted in more than one instance?
One solution is to design a aggregator on top of __location aggregator__ instances 
that deals with the issue of querying their partial view of world. Which means that this aggregator should
be aware of how data is partitioned by kafka among those instance. Such approach has plenty of problems that will 
be explained in the report later.
To avoid it, we will save the __location aggregator__ state into a global store. Which means all the tasks of this operator
will have the same view of the world. As a result all the H2 databases will have the same data. 
So regardless of which instance query hits, the response will be the same.

Also, state of __location aggregator__ tasks are eventually consistent. 
Because they relay on a commit-log (change log topic) for data replication. 
More details about Kafka streams and how we used it to achieve eventual consistency will be
provided in report later. 

#### Physical data flow diagram
The picture below roughly illustrate how we are going to deploy tasks.
Each colored group of tasks, represents a separate deployable unit. 
The units with same shape correspond to same operations.

![Physical data flow](/work-report/physical%20data%20flow%20diagram.png)

### Expectations
FenseX is expected to 
 * handle varying loads successfully and smoothly
 * low latency queries 
 * low latency high throughput process of fence:location intersections
 * be highly available (resiliency, failure isolation)
 * easy to scale out and scale in? (even automatic)
 * using resources optimally (local and overall) (like not relaying on blocking communication)
 * meet the non-functional requirements of mission critical geofencing use cases as well as casual ones
 * flexible (for example: any in-memory co-locatable database can be replaced by H2)
 * can recovery from major failure (kafka topics are source of truth that are persisted durably on disk. Any state can
 be re-build just by re deploying the system and re iterating over the events in kafka topics). 

### three four research question (higher throughput, eventual consistency achieved, resiliency?, ...) *based ( on what) (and why)
