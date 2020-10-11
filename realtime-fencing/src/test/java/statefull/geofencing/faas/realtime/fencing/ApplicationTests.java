package statefull.geofencing.faas.realtime.fencing;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import statefull.geofencing.faas.realtime.fencing.config.Stores;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "mover-updates",
        "${spring.application.name}"+"-changelog-"+Stores.FENCE_STATE_STORE,
        "event_log"
})
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

}
