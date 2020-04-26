package wonderland.faas.stateful.geofencing.streamprocessing;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wonderland.faas.stateful.geofencing.CustomSerdes;
import wonderland.faas.stateful.geofencing.config.Topics;
import wonderland.faas.stateful.geofencing.dto.MoverPositionUpdate;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class UpdateProducer {

    private KafkaProducer<String, MoverPositionUpdate> positionUpdateProducer;

    public UpdateProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        var providerConfig = new Properties();
        providerConfig.put("bootstrap.servers", bootstrapServers);
        positionUpdateProducer = new KafkaProducer<>(providerConfig, new StringSerializer(), CustomSerdes.MOVER_POSITION_UPDATE_JSON_SERDE.serializer());
    }

    public void producePositionUpdate(String key, double latitude, double longitude) {
        try {
            var value = MoverPositionUpdate.newBuilder()
                    .withLatitude(latitude)
                    .withLongitude(longitude)
                    .withMoverId(key)
                    .withTimestamp(Instant.now())
                    .build();
            var record = new ProducerRecord<>(Topics.MOVER_POSITION_UPDATES_TOPIC, key, value);
            positionUpdateProducer.send(record).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
