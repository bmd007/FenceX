# Stateful Geofencing Faas

## Proposal

Geofencing queries usually take special treatments since they relay upon a special type of index. This type of index is more CPU intensive than, for instance, index for plain text. So latency is an important factor in geofencing queries. One of the solutions to reduce the latency is to use in memory databases to avoid IO latency (which is considerably higher than computational latency relatively). 
But the first challenge with in memory data bases is to use them in a resilient way. Resiliency usually comes with replication. And replication beings up the holy inconsistency problem. 
Since most of the in memory databases are not designed fundamentally to be deployed with more than one instance and are not intended for horizontal scalability, so far we just exchanged IO for inconsistency.

One of the aspects of this project is to propose a solution for keeping consistency in an in memory database "cluster". The type of consistency is eventual which is enough here. We will talk later about the non functional boundaries of this project. 
The foundation is to use a journal/change log which is one of the most famous eventual consistency solutions in databases realm. Usually databases implement their own change log. Here we will use Kafka topics through a stream processing framework called Kafka Streams. Kafka Streams is a full fledged stream processing framework that supports stateful processors. By default the database below the state stores of Kafka Streams is rocks db. And Kafka Stream uses it as a key value store. In this project we plug our custom data store into Kafka Streams which uses H2. H2 is an in memory database that supports geo spatial index and query. 
We define a global KTable in Kafka Streams on top of our state store which means all the instances of the application will have the same view over data (in that KTable).

Preparing a deployment environment, managing brokers, installing and using orchestration tools, selecting right database and enabling CI/CD are neither cheap nor simple for not large organizations, among other tasks before everything becomes ready to deploy a peice of code into production. There is a group of organizations/users that need development and computation environment/platform but their need to cost ratios is not high enough to setup a thier own environment or even use Paas. Serverless/Faas has been recently the emerging idea to provide cheap computation environment/platform for such group of organizations/users. AWS lambda is the most famous implementation of this idea. But the currently available solutions are mainly state less and there is no easy well managed access to a database in the scope of functions. As a consequence so far such stateless Faas services has been used for stateless task. 
The statelessness of theese functions means that they turn an input into an output without carring about what happened in the past. The de facto example is creating thumbnails. But what if we want a system that calculates the average of room temperature in real time and our temperature sensor produces samples every minute; And we want to use Faas to keep it cheap.
In this project we designed a statefull Faas platform using Kafka Streams. It's amazing how stream processing concepts has counter parts in functional programming world. By mixing these two concepts and a touch of magic from Spring framework family, we implemented a state full Faas platform. This platform is targeting spatial operations and uses/provides H2 as the underlying database.

In the lowest level of abstraction, users should provide a Jar file which contains implementation of some specific functions (filter, aggregate, wrap, query). And in highest level of abstraction, users write the code of that functions in a Web UI.


Other parts of this project other than lowest level of abstraction  can be:
- Autoscalling solutions 
- Making core data type more flexible 
- Aws/Google/Azure integration 
- Load testing
- Cloud Foundry integration 
- Examining support of JVM languages other than Java


## References:
 - Ben Stopford, Designing Event-Driven Systems Concepts and Patterns for Streaming Services with Apache Kafka
 - Jay Kreps, I ♥ Logs
 - Martin Kleppmann, Making Sense of Stream Processing The Philosophy Behind Apache Kafka and Scalable Stream Data Platforms
 - Mohammed Al-Ameen1, Josef Spillner,  Systematic and open exploration of FaaS and Serverless Computing research
 - van Eyk, E., Scheuner, J., Eisman, S. et al (2020) Beyond Microbenchmarks: The SPEC-RG Vision for a Comprehensive Serverless Benchmark Companion of the ACM/SPEC International Conference on Performance Engineering: http://dx.doi.org/10.1145/3375555.3384381
 - Erwin van Eyk1, and Alexandru Iosup, Simon Seif, and Markus Thömmes. 2017. The SPEC Cloud Group’s Research Vision on FaaS and Serverless Architectures. In Proceedings of WoSC’: Workshop on Serverless Computing, Las Vegas, NV, 2017 (WoSC’17), https://doi.org/10.1145/3154847.3154848
 - Fabian Hueske, Vasiliki Kalavri, Stream Processing with Apache Flink Fundamentals, Implementation, and Operation of Streaming Applications 
 - Vikram Sreekanti, Chenggang Wu, Xiayue Charles Lin, Johann Schleier-Smith, Jose M. Faleiro, Joseph E. Gonzalez, Joseph M. Hellerstein, Alexey Tumanov, 2020, Cloudburst: Stateful Functions-as-a-Service
 - Serverless Computing: One Step Forward, Two Steps BackJoseph M. Hellerstein, Jose Faleiro, Joseph E. Gonzalez, Johann Schleier-Smith, Vikram Sreekanti,Alexey Tumanov and Chenggang Wu
