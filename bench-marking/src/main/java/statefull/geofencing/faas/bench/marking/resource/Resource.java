package statefull.geofencing.faas.bench.marking.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import statefull.geofencing.faas.bench.marking.client.LocationAggregateClient;
import statefull.geofencing.faas.bench.marking.client.LocationUpdatePublisherClient;
import statefull.geofencing.faas.bench.marking.client.RealTimeFencingClient;
import statefull.geofencing.faas.bench.marking.client.model.FenceDto;
import statefull.geofencing.faas.bench.marking.client.model.MoverLocationUpdate;
import statefull.geofencing.faas.bench.marking.domain.TripDocument;
import statefull.geofencing.faas.bench.marking.repository.TripDocumentRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final AtomicBoolean isStreaming = new AtomicBoolean(Boolean.FALSE);

    @GetMapping("/all/ongoing/{play}")
    public void testOnGoingStream(@PathVariable String play) {
        if (!play.equals("play")) {
            isStreaming.set(Boolean.FALSE);
            return;
        } else {
            isStreaming.set(Boolean.TRUE);
        }
        Flux.interval(Duration.ofSeconds(5))
                .filter(ignore -> isStreaming.get())
                .doOnNext(ignore -> loadTestNumberOfTimes(1, "both"))
                .subscribe();
    }

    @GetMapping("/all/times/{times}/leg/{leg}")
    public void loadTestNumberOfTimes(@PathVariable Integer times, @PathVariable String leg) {
        repository.findAll()
                .delayUntil(this::defineFence)
                .collectList()
                .subscribe(ignore -> {
                    for (int i = 0; i < times; i++) {
                        if (leg.equals("push")) {
                            testAllTrips_PushLeg();
                        } else if (leg.equals("poll")) {
                            testAllTrips_PollLeg();
                        } else {
                            testAllTrips_bothLegs();
                        }
                    }
                });
    }

    private Mono<String> defineFence(TripDocument tripDocument) {
        return fencingClient.defineFenceForMover(FenceDto.newBuilder()
                .withMoverId(tripDocument.getTripId())
                .withWkt(tripDocument.getMiddleRouteRingWkt())
                .build());
    }

    public void testAllTrips_bothLegs() {
        repository.findAll()
                .flatMap(tripDocument -> Flux.fromIterable(tripDocument.getLocationReports())
                        .subscribeOn(Schedulers.boundedElastic())
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
                .subscribe();
    }

    public void testAllTrips_PollLeg() {
        repository.findAll()
                .flatMap(tripDocument -> Flux.fromIterable(tripDocument.getLocationReports())
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnNext(locationReport -> locationAggregateClient
                                .queryMoverLocationsByFence(tripDocument.getMiddleRouteRingWkt())
                                .subscribe()))
                .subscribe();
    }


    public void testAllTrips_PushLeg() {
        repository.findAll()
                .flatMap(tripDocument -> Flux.fromIterable(tripDocument.getLocationReports())
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnNext(locationReport -> updatePublisherClient.requestLocationUpdate(MoverLocationUpdate.newBuilder()
                                .withLatitude(locationReport.getLatitude())
                                .withLongitude(locationReport.getLongitude())
                                .withMoverId(tripDocument.getTripId())
                                .withTimestamp(Instant.now())
                                .build())
                                .subscribe()))
                .subscribe();
    }

}


