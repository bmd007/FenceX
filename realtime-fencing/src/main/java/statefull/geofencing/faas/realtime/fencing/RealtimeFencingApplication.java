package statefull.geofencing.faas.realtime.fencing;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = {"statefull.geofencing.faas.common.domain",
"statefull.geofencing.faas.common.dto", "statefull.geofencing.faas.function", "statefull.geofencing.faas.realtime.fencing"})
public class RealtimeFencingApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeFencingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RealtimeFencingApplication.class, args);
    }

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() {

    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {

    }
}
