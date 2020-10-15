package statefull.geofencing.faas.bench.marking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import statefull.geofencing.faas.bench.marking.domain.LocationReport;
import statefull.geofencing.faas.bench.marking.domain.TripDocument;
import statefull.geofencing.faas.bench.marking.dto.TripDataDto;
import statefull.geofencing.faas.bench.marking.repository.TripDocumentRepository;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.BaseStream;

@SpringBootApplication(scanBasePackages = {"statefull.geofencing.faas"})
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    static Map<String, TripDocument> MAP = new ConcurrentHashMap<>();

    @Autowired
    TripDocumentRepository repository;

    @Autowired
    ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        var file = new File("H:\\JAVA ProgRAming\\Intellij_workSpace\\statefull-geofencing-faas\\bench-marking\\trip-data.json").toPath();
        Flux.using(() -> Files.lines(file), Flux::fromStream, BaseStream::close)
//        .subscribeOn(Schedulers.single())
        .map(line -> {
            try {
                return objectMapper.reader().forType(TripDataDto.class).<TripDataDto>readValue(line);
            } catch (Exception e) {
                LOGGER.error("error: for line {}", line, e);
                return null;
            }
        })
        .bufferUntilChanged(tripDataDto -> tripDataDto.getTripRefNumber())
        .doOnNext(tripDataDtos -> {
                    var tripId = tripDataDtos.get(0).getTripRefNumber();
                    Flux.fromIterable(tripDataDtos)
                            .map(tripDataDto -> LocationReport.define(tripDataDto.getTimestamp(), tripDataDto.getLatitude(), tripDataDto.getLongitude()))
                            .sort((o1, o2) -> o1.getTimestamp().isBefore(o2.getTimestamp()) ? 1 : -1)
                            .collectList()
                            .map(locationReports -> TripDocument.newBuilder().withLocationReports(locationReports).withTripId(tripId).build())
                            .flatMap(repository::save)
                            .subscribe(tripDocument -> LOGGER.info("saved {} with report size {}", tripDocument.getTripId(),
                                    tripDocument.getLocationReports().size()));
                }
        )
        .collectList()
        .flatMap(lists -> repository.count())
        .subscribe(aLong -> LOGGER.info("repo size "+ aLong));
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {

    }
}
