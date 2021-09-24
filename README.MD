# Stateful Geofencing Faas

## Abstract
To check if a coordinate (location, point on a map) is within a specific area is called geofencing.
Since the emergence of smartphones, smart cars, and other mobile objects with GPS sensor attached to them,
functional and non-functional requirements of geofencing systems have significantly changed.
Previously, the number of mobile objects and thus the size of geospatial data about them and their load on the
geofencing systems were much lower.
Also, geofencing services were not much integrated into the day-to-day life of nonprofessional users.
Nowadays, on the other hand, geofencing systems are required to process large loads of requests and data in
realtime with a much higher availability while serving a wider range of users.
Online taxi providers, elderly care platforms, pet tracking systems, and disaster management systems are examples
of use cases in which geofencing with aforementioned non-functional requirements is in action.
In this thesis we have designed and implemented a geofencing system called FenceX with those requirements in mind.
The main pillars of FenceX, which allow for satisfaction of those requirements, are stream processing and
microservices.
FenceX has shown weak and strong scalability characteristics while being evaluated using real taxi trip data.
Apart from being a geofencing system, FenceX can be considered an implementation for the idea of stateful function
as a service (FaaS).
Currently the available FaaS providers, Lambda from AWS most famously, do not allow for stateful functions.
In this thesis, thanks to the usage of Kafka Stream as the stream processing framework and its supports for stateful
operations, FenceX allows for the definition and deployment of stateful functions.
While comparing FenceX with related works, we observed better results in multiple dimensions, peak throughput
and resiliency for example.

Please read `./work-report/report-draft1.tex` for more details


#### Technical requirement for starting development in this repo:
 1- Due to limitations from spring-boot.experimental-thingJar, you have to use Java 15 during developemnt (for building project).