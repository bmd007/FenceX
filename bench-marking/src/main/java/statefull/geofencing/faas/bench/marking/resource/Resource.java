package statefull.geofencing.faas.bench.marking.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
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

    public Resource(TripDocumentRepository repository, LocationUpdatePublisherClient updatePublisherClient, RealTimeFencingClient fencingClient) {
        this.repository = repository;
        this.updatePublisherClient = updatePublisherClient;
        this.fencingClient = fencingClient;
    }

    @GetMapping("/all")
    public Flux loadTest() {
        return repository.findAll()
                .subscribeOn(Schedulers.parallel())
                .doOnNext(tripDocument -> fencingClient.defineFenceForMover(FenceDto.newBuilder()
                        .withMoverId(tripDocument.getTripId())
                        .withWkt(tripDocument.getMiddleRouteRingWkt())
                        .build()))
                .collectList()
                .flatMapIterable(Function.identity())
                .flatMap(tripDocument -> Flux.fromIterable(tripDocument.getLocationReports())
                        .subscribeOn(Schedulers.parallel())
                        .doOnNext(report -> updatePublisherClient.requestLocationUpdate(MoverLocationUpdate.newBuilder()
                                .withLatitude(report.getLatitude())
                                .withLongitude(report.getLongitude())
                                .withMoverId(tripDocument.getTripId())
                                .withTimestamp(Instant.now())
                                .build())));
    }

    @GetMapping("/one/{id}")
    public void loadTestForOne(@PathVariable String id) {
        repository.findById(id)
                .delayUntil(tripDocument -> fencingClient.defineFenceForMover(FenceDto.newBuilder()
                        .withMoverId(tripDocument.getTripId())
                        .withWkt(tripDocument.getMiddleRouteRingWkt())
                        .build()))
                .delayUntil(tripDocument ->
                        Flux.fromIterable(tripDocument.getLocationReports())
                                .delayUntil(report -> updatePublisherClient.requestLocationUpdate(MoverLocationUpdate.newBuilder()
                                        .withLatitude(report.getLatitude())
                                        .withLongitude(report.getLongitude())
                                        .withMoverId(tripDocument.getTripId())
                                        .withTimestamp(Instant.now())
                                        .build()))
                                .map(unused -> tripDocument))
                //todo query location update processor for last location being on the trip route
                .subscribe(tripDocument -> {
                    LOGGER.info("trip document {} info published", tripDocument.getTripId());
                });
    }
}
