package statefull.geofencing.faas.location.update.publisher.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;
import statefull.geofencing.faas.location.update.publisher.config.Topics;
import statefull.geofencing.faas.location.update.publisher.dto.TimeLessMoverLocationUpdate;

import java.time.Instant;

@RestController
@RequestMapping("/api/location")
public class LocationUpdateResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUpdateResource.class);
    KafkaTemplate<String, MoverLocationUpdate> kafkaTemplate;

    public LocationUpdateResource(KafkaTemplate<String, MoverLocationUpdate> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/update")
    public void locationUpdate(@RequestBody MoverLocationUpdate locationUpdate) {
        kafkaTemplate.send(Topics.MOVER_POSITION_UPDATES_TOPIC, locationUpdate.getMoverId(), locationUpdate)
                .addCallback(
                        result -> LOGGER.info("location update {} published", locationUpdate),
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
        kafkaTemplate.send(Topics.MOVER_POSITION_UPDATES_TOPIC, locationUpdate.getMoverId(), locationUpdate)
                .addCallback(
                        result -> LOGGER.info("location update {} published", locationUpdate),
                        ex -> LOGGER.error("Couldn't publish {}", locationUpdate, ex));
    }
}
