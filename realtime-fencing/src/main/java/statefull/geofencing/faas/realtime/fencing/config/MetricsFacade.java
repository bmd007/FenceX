package statefull.geofencing.faas.realtime.fencing.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsFacade {
    private final Counter moverFenceIntersectionCounter;

    public MetricsFacade(MeterRegistry registry) {
        moverFenceIntersectionCounter = registry.counter("geofencing.mover.fence.intersection.counter");
    }

    public void incrementMoverFenceIntersectionCounter() {
        moverFenceIntersectionCounter.increment();
    }

}
