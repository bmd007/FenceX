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
 
### Large Scale Indexing of Geofences, 2014
https://ieeexplore.ieee.org/abstract/document/6910110

In this article, a poll style geofencing system is introduced that supports large scales
of data and load. Dynamic caching and work load distribution over multiple instances
of workers are the base of achieved scalability. Each worker instance is
responsible for indexing a region of the world, which allows for lower query and index latency.
Each worker instance has only one thread which removed the need for thread safe data structures and synchronization solutions.
Also, each worker instance takes care of areas around it's dedicated region as a solution for border problems.

### Using Complex Event Processing for implementing a geofencing service, 2013
https://ieeexplore.ieee.org/abstract/document/6662608

This article illustrates a push style geofencing system that relies on a CEP (complex event processing) 
for fence entrance/exit detection. This CEP is also used for detecting other patterns like 
*user left the fence and returned within 1 minute*. 
The architecture of this system apart from using a CEP engine, is not more significant than 3 tiers (layered).
The authors evaluations resulted in the system handling more than 20,000 event/sec with 1000 queries active in a single CEP instance.
The unit of concurrency of the system was not clear in the article. So it's not clear what are the bounds for scalability of the introduced system.
 
What CEPs can do with events is pretty much similar to functional programming operations like:
 filtering, translation, splitting, aggregation, composition
 
### Stream processing with apache flink fundamentals (chapter1) 
book, 2017, Fabian Hueske and Vasiliki Kalavri

Data flow graph is a useful tool to express and study how data is flown in a stream processing system.
There are two type of data flow graph: logical and physical. 
In a logical data flow graph nodes represent `operators` like sum, count, filter, aggregate and ... .
It doesn't tell anything about deployment.
On the other hand, a physical data flow graph illustrates how exactly the deployed instances look in one image.
Like how many instances of each `task` will be deployed. Not to forget that nodes are called `task` in the physical data flow graph.
     

![Physical data flow](/work-report/physical-data-flow-graph.png)
Format: ![Alt Text](url)





