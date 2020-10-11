package statefull.geofencing.faas.realtime.fencing;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import statefull.geofencing.faas.common.domain.Coordinate;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.realtime.fencing.config.Topics;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class MoverUpdateProducer {

    private final KafkaProducer<String, Mover> moverUpdateProducer;

    public MoverUpdateProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        var providerConfig = new Properties();
        providerConfig.put("bootstrap.servers", bootstrapServers);
        moverUpdateProducer = new KafkaProducer<>(providerConfig, new StringSerializer(), CustomSerdes.MOVER_JSON_SERDE.serializer());
    }

    public void producePositionUpdate(String key, double latitude, double longitude) {
        try {
            var value = Mover.newBuilder()
                    .withLastLocation(Coordinate.builder()
                            .withLatitude(latitude)
                            .withLongitude(longitude)
                            .build())
                    .withId(key)
                    .withUpdatedAt(Instant.now())
                    .build();
            var record = new ProducerRecord<>(Topics.MOVER_UPDATES_TOPIC, key, value);
            moverUpdateProducer.send(record).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
