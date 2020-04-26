package wonderland.faas.stateful.geofencing;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static wonderland.faas.stateful.geofencing.config.Stores.MOVER_IN_MEMORY_STATE_STORE;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "mover-position-updates",
        MOVER_IN_MEMORY_STATE_STORE + "-" + "stateful-geofencing-faas-changelog",
        "event_log"
})
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

}
