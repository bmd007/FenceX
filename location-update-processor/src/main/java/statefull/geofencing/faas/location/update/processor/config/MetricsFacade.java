package statefull.geofencing.faas.location.update.processor.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class MetricsFacade {
    private final Counter moverAggregationCounter;
//    private MeterRegistry registry;

    public MetricsFacade(MeterRegistry registry) {
//        this.registry = registry;
        moverAggregationCounter = registry.counter("geofencing.mover.aggregation.counter");
    }

    public void incrementAggregationCounter(){
        moverAggregationCounter.increment();
    }

}
