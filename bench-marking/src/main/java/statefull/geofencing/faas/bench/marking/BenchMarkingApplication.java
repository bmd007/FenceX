package statefull.geofencing.faas.bench.marking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;
import statefull.geofencing.faas.bench.marking.domain.LocationReport;
import statefull.geofencing.faas.bench.marking.domain.TripDocument;
import statefull.geofencing.faas.bench.marking.dto.TripDataDto;
import statefull.geofencing.faas.bench.marking.repository.TripDocumentRepository;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.BaseStream;

@SpringBootApplication
public class BenchMarkingApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchMarkingApplication.class);

    @Autowired
    TripDocumentRepository repository;

    @Autowired
    ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(BenchMarkingApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("trip-data.json");
        var file = new File(resource.getFile()).toPath();
        Flux.using(() -> Files.lines(file), Flux::fromStream, BaseStream::close)
                .map(line -> {
                    try {
                        return objectMapper.reader().forType(TripDataDto.class).<TripDataDto>readValue(line);
                    } catch (Exception e) {
                        LOGGER.error("error: for line {}", line, e);
                        return null;
                    }
                })
                .bufferUntilChanged(tripDataDto -> tripDataDto.getTripRefNumber())//be careful: this approach only make
                // senses when in the source file, all of the records related to one trip are all after each other
                // and different trips do not intervene each other's sequence of rows.
                .filter(tripData -> tripData.size() >= 10)
                .doOnNext(tripData -> {
                            var tripId = tripData.get(0).getTripRefNumber();
                            Flux.fromIterable(tripData)
                                    .map(tripDataDto -> LocationReport.define(tripDataDto.getTimestamp(), tripDataDto.getLatitude(), tripDataDto.getLongitude()))
                                    .sort((o1, o2) -> o1.getTimestamp().isAfter(o2.getTimestamp()) ? 1 : -1)
                                    .collectList()
                                    .map(locationReports -> TripDocument.newBuilder().withLocationReports(locationReports).withTripId(tripId).build())
                                    .flatMap(TripDocument::populateWktRoute)
                                    .map(TripDocument::populateMiddleRouteRingWkt)
                                    .flatMap(repository::save)
                                    .subscribe(tripDocument -> LOGGER.info("saved {} with report size {}", tripDocument.getTripId(),
                                            tripDocument.getLocationReports().size()));
                        }
                )
                .collectList()
                .flatMap(lists -> repository.count())
                .subscribe(aLong -> LOGGER.info("repo size " + aLong));
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {

    }
}
