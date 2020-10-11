package statefull.geofencing.faas.realtime.fencing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import statefull.geofencing.faas.realtime.fencing.config.Stores;
import statefull.geofencing.faas.realtime.fencing.util.KafkaStreamsAwait;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "mover-position-updates",
        "${spring.application.name}"+"-changelog-"+Stores.FENCE_STATE_STORE,
        "event_log"
})
@DirtiesContext
@Disabled("Test works locally however fails in Jenkins.")
// The reason of failure is:
// StreamsException: Could not lock global state directory. This could happen if multiple KafkaStreams instances are running on the same host using the same state directory.
class StreamsIntegrationTest {

    @Autowired
    KafkaStreamsAwait await;

    @Autowired
    MoverUpdateProducer producer;

    @BeforeEach
    void setUp() throws Exception {
        await.await();
    }

    @Test
    void test() throws Exception {

    }

}
