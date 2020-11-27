package statefull.geofencing.faas.location.aggregate.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;


@Component
public class MetricsFacade {

    private final Counter moverAggregationCounter;

    public MetricsFacade(MeterRegistry registry) {
        moverAggregationCounter = registry.counter("geofencing.mover.aggregation.counter");
    }

    public void incrementAggregationCounter() {
        moverAggregationCounter.increment();
    }

}
