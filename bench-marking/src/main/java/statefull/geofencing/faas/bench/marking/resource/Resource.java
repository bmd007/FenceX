package statefull.geofencing.faas.bench.marking.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import statefull.geofencing.faas.bench.marking.client.LocationAggregateClient;
import statefull.geofencing.faas.bench.marking.client.LocationUpdatePublisherClient;
import statefull.geofencing.faas.bench.marking.client.RealTimeFencingClient;
import statefull.geofencing.faas.bench.marking.client.model.FenceDto;
import statefull.geofencing.faas.bench.marking.client.model.MoverLocationUpdate;
import statefull.geofencing.faas.bench.marking.repository.TripDocumentRepository;

import java.time.Instant;
import java.util.function.Function;

@RestController
@RequestMapping("/api/test")
public class Resource {

    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);
    private final TripDocumentRepository repository;
    private final LocationUpdatePublisherClient updatePublisherClient;
    private final RealTimeFencingClient fencingClient;
    private final LocationAggregateClient locationAggregateClient;

    public Resource(TripDocumentRepository repository, LocationUpdatePublisherClient updatePublisherClient, RealTimeFencingClient fencingClient, LocationAggregateClient locationAggregateClient) {
        this.repository = repository;
        this.updatePublisherClient = updatePublisherClient;
        this.fencingClient = fencingClient;
        this.locationAggregateClient = locationAggregateClient;
    }

    @GetMapping("/all/times/{times}/leg}")
    public void loadTestNumberOfTimes(@PathVariable Integer times, @PathVariable String leg) {
        for (int i = 0; i < times; i++) {
            if (leg.equals("push")) {
                testAllTrips_PushLeg();
            } else if (leg.equals("poll")) {
                testAllTrips_PollLeg();
            } else {
                testAllTrips_AllLegs();
            }
        }
    }

    @GetMapping("/all")
    public void testAllTrips_AllLegs() {
        repository.findAll()
                .delayUntil(tripDocument -> fencingClient.defineFenceForMover(FenceDto.newBuilder()
                        .withMoverId(tripDocument.getTripId())
                        .withWkt(tripDocument.getMiddleRouteRingWkt())
                        .build()))
                .collectList()
                .flatMapIterable(Function.identity())
                .flatMap(tripDocument -> Flux.fromIterable(tripDocument.getLocationReports())
                        .doOnNext(locationReport -> updatePublisherClient.requestLocationUpdate(MoverLocationUpdate.newBuilder()
                                .withLatitude(locationReport.getLatitude())
                                .withLongitude(locationReport.getLongitude())
                                .withMoverId(tripDocument.getTripId())
                                .withTimestamp(Instant.now())
                                .build())
                                .subscribe())
                        .doOnNext(locationReport -> locationAggregateClient
                                .queryMoverLocationsByFence(tripDocument.getMiddleRouteRingWkt())
                                .subscribe()))
                .subscribe(locationReport -> LOGGER.info("{} is published and queried", locationReport));
    }

    @GetMapping("/all/leg/poll")
    public void testAllTrips_PollLeg() {
        repository.findAll()
                .delayUntil(tripDocument -> fencingClient.defineFenceForMover(FenceDto.newBuilder()
                        .withMoverId(tripDocument.getTripId())
                        .withWkt(tripDocument.getMiddleRouteRingWkt())
                        .build()))
                .collectList()
                .flatMapIterable(Function.identity())
                .flatMap(tripDocument -> Flux.fromIterable(tripDocument.getLocationReports())
                        .doOnNext(locationReport -> locationAggregateClient
                                .queryMoverLocationsByFence(tripDocument.getMiddleRouteRingWkt())
                                .subscribe()))
                .subscribe(locationReport -> LOGGER.info("{} is queried", locationReport));
    }


    @GetMapping("/all/leg/push")
    public void testAllTrips_PushLeg() {
        repository.findAll()
                .delayUntil(tripDocument -> fencingClient.defineFenceForMover(FenceDto.newBuilder()
                        .withMoverId(tripDocument.getTripId())
                        .withWkt(tripDocument.getMiddleRouteRingWkt())
                        .build()))
                .collectList()
                .flatMapIterable(Function.identity())
                .flatMap(tripDocument -> Flux.fromIterable(tripDocument.getLocationReports())
                        .doOnNext(locationReport -> updatePublisherClient.requestLocationUpdate(MoverLocationUpdate.newBuilder()
                                .withLatitude(locationReport.getLatitude())
                                .withLongitude(locationReport.getLongitude())
                                .withMoverId(tripDocument.getTripId())
                                .withTimestamp(Instant.now())
                                .build())
                                .subscribe()))
                .subscribe(locationReport -> LOGGER.info("{} is published", locationReport));
    }


    @GetMapping("/one/{id}")
    public void testOneTrip(@PathVariable String id) {
        repository.findById(id)
                .delayUntil(tripDocument -> fencingClient.defineFenceForMover(FenceDto.newBuilder()
                        .withMoverId(tripDocument.getTripId())
                        .withWkt(tripDocument.getMiddleRouteRingWkt())
                        .build()))
                .delayUntil(tripDocument ->
                        Flux.fromIterable(tripDocument.getLocationReports())
                                .doOnNext(report -> updatePublisherClient.requestLocationUpdate(MoverLocationUpdate.newBuilder()
                                        .withLatitude(report.getLatitude())
                                        .withLongitude(report.getLongitude())
                                        .withMoverId(tripDocument.getTripId())
                                        .withTimestamp(Instant.now())
                                        .build()).subscribe())
                                .doOnNext(locationReport -> locationAggregateClient.queryMoverLocationsByFence(
                                        tripDocument.getMiddleRouteRingWkt()).subscribe()))
                .subscribe(tripDocument ->
                        LOGGER.info("trip document {} info published and queried", tripDocument.getTripId()));
    }
}


