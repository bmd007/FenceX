package statefull.geofencing.faas.location.update.publisher.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;
import statefull.geofencing.faas.location.update.publisher.config.MetricsFacade;
import statefull.geofencing.faas.location.update.publisher.config.Topics;
import statefull.geofencing.faas.location.update.publisher.dto.TimeLessMoverLocationUpdate;

import java.time.Instant;

@RestController
@RequestMapping("/api/location")
public class LocationUpdateResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUpdateResource.class);
    private KafkaTemplate<String, MoverLocationUpdate> kafkaTemplate;
    private final MetricsFacade metrics;

    public LocationUpdateResource(KafkaTemplate<String, MoverLocationUpdate> kafkaTemplate, MetricsFacade metrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.metrics = metrics;
    }

    @PostMapping("/update")
    public void locationUpdate(@RequestBody MoverLocationUpdate locationUpdate) {
        kafkaTemplate.send(Topics.MOVER_POSITION_UPDATES_TOPIC, locationUpdate.getMoverId(), locationUpdate)
                .addCallback(
                        result -> {
                            LOGGER.info("location update {} published", locationUpdate);
                            metrics.incrementLocationUpdatePublishedCounter();
                        },
                        ex -> LOGGER.error("Couldn't publish {}", locationUpdate, ex));
    }

    @PostMapping("/update/now")
    public void locationUpdate(@RequestBody TimeLessMoverLocationUpdate timeLessLocationUpdate) {
        var locationUpdate = MoverLocationUpdate.newBuilder()
                .withLatitude(timeLessLocationUpdate.getLatitude())
                .withLongitude(timeLessLocationUpdate.getLongitude())
                .withMoverId(timeLessLocationUpdate.getMoverId())
                .withTimestamp(Instant.now())
                .build();
        locationUpdate(locationUpdate);
    }
}
