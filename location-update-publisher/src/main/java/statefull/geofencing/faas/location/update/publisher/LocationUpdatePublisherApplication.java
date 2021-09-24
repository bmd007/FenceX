package statefull.geofencing.faas.location.update.publisher;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.net.UnknownHostException;

@SpringBootApplication
public class LocationUpdatePublisherApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUpdatePublisherApplication.class);

//    @Bean
//    public TimedAspect timedAspect(MeterRegistry registry) {
//        return new TimedAspect(registry);
//    }

    public static void main(String[] args) {
        SpringApplication.run(LocationUpdatePublisherApplication.class, args);
    }

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() {
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {

    }
}
