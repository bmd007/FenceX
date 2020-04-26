package wonderland.faas.stateful.geofencing.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

import static org.apache.kafka.streams.KafkaStreams.State.*;

@Component
public class KafkaStreamsHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsHealthIndicator.class);

    private final String messageKey = "kafka-streams";

    private StreamsBuilderFactoryBean streams;
    private boolean stillRunning = false;

    public KafkaStreamsHealthIndicator(StreamsBuilderFactoryBean streams) {
        this.streams = streams;
    }

    private synchronized boolean isStillRunning() {
        return stillRunning;
    }

    private synchronized void setStillRunning(boolean stillRunning) {
        this.stillRunning = stillRunning;
    }

    @Override
    public Health health() {
        if (streams.isRunning() && isStillRunning()) {
            return Health.up().withDetail(messageKey, "Available").build();
        }
        return Health.down().withDetail(messageKey, "Not Available").build();
    }

    @PostConstruct
    public void stateAndErrorListener() {
        streams.setUncaughtExceptionHandler((t, e) -> LOGGER.error("uncaught error on kafka streams", e));
        streams.setStateListener((newState, oldState) -> {
            LOGGER.info("transit kafka streams state from {} to {}", oldState, newState);
            setStillRunning(Set.of(REBALANCING, RUNNING, CREATED).contains(newState));
        });
    }
}
