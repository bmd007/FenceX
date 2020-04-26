package wonderland.faas.stateful.geofencing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.kafka.common.config.TopicConfig.*;

/**
 * Configuration class to automatically create the topics with the configured partitions and replication factor.
 */
@Configuration
@Profile("!test")
public class TopicCreator {

    private PartitionDef eventTopicDefinition;
    private PartitionDef changeLogTopicDefinition;
    private String applicationName;

    public TopicCreator(
            @Value("${spring.application.name}") String applicationName,
            @Value("${kafka.topic-partitions.eventTopic}") String eventTopicDefinition,
            @Value("${kafka.topic-partitions.changeLogTopic}") String changeLogTopicDefinition) {
        this.applicationName = applicationName;
        this.eventTopicDefinition = PartitionDef.parse(eventTopicDefinition);
        this.changeLogTopicDefinition = PartitionDef.parse(changeLogTopicDefinition);

    }

    public static String storeTopicName(String storeName, String applicationName) {
        return String.format("%s-%s-changelog", applicationName, storeName);
    }

    @Bean
    public NewTopic eventsTopic() {
        return new NewTopic(Topics.EVENT_LOG, eventTopicDefinition.numPartitions, eventTopicDefinition.replicationFactor)
                .configs(Map.of(RETENTION_MS_CONFIG, "-1", RETENTION_BYTES_CONFIG, "-1"));
    }

    @Bean
    public NewTopic moverStateStoreTopic() {
        return new NewTopic(storeTopicName(Stores.MOVER_IN_MEMORY_STATE_STORE, applicationName), changeLogTopicDefinition.numPartitions, changeLogTopicDefinition.replicationFactor)
                .configs(Map.of(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT));
    }

    private static class PartitionDef {

        private final static Pattern PATTERN = Pattern.compile("(\\d+):(\\d+)");

        private int numPartitions;
        private short replicationFactor;

        private PartitionDef(int numPartitions, short replicationFactor) {
            this.numPartitions = numPartitions;
            this.replicationFactor = replicationFactor;
        }

        public static PartitionDef parse(String value) {
            var matcher = PATTERN.matcher(value);
            if (matcher.matches()) {
                var numParts = Integer.parseInt(matcher.group(1));
                var repFactor = Short.parseShort(matcher.group(2));
                return new PartitionDef(numParts, repFactor);
            } else {
                throw new IllegalArgumentException("Invalid topic partition definition: " + value);
            }
        }
    }
}
