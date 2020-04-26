package wonderland.faas.stateful.geofencing;

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
import wonderland.faas.stateful.geofencing.domain.Coordinate;
import wonderland.faas.stateful.geofencing.repository.MoverRepository;
import wonderland.faas.stateful.geofencing.streamprocessing.UpdateProducer;
import wonderland.faas.stateful.geofencing.util.KafkaStreamsAwait;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static wonderland.faas.stateful.geofencing.config.Stores.MOVER_IN_MEMORY_STATE_STORE;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "mover-position-updates",
        MOVER_IN_MEMORY_STATE_STORE + "-" + "stateful-geofencing-faas-changelog",
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
    MoverRepository repository;

    @Autowired
    UpdateProducer producer;

    @BeforeEach
    void setUp() throws Exception {
        await.await();
    }

    @Test
    void test() throws Exception {
        var empty = repository.getAll();
        assertTrue(empty.isEmpty());
        for (var i = 0; i < 100; i++) {
            producer.producePositionUpdate(String.format("ABC%03d", i), 10.1, 20.2);
        }
        await()
                .atMost(100, SECONDS)
                .until(() -> repository.count() > 0);
        var all = repository.getAll();
        assertFalse(all.isEmpty());
        all.forEach(v -> assertEquals(new Coordinate(10.1, 20.2), v.getPosition()));
        all.forEach(v -> assertEquals(true, v.getAvailability().isAvailable()));
    }

}
