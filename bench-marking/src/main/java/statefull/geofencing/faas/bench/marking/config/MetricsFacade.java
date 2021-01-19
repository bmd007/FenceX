package statefull.geofencing.faas.bench.marking.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsFacade {

    private final Counter successfullySentQueryByFenceCounter;
    private final Counter failedQueryByFenceCounter;

    public MetricsFacade(MeterRegistry registry) {
        successfullySentQueryByFenceCounter = registry.counter("geofencing.mover.query.sent.ok.counter");
        this.failedQueryByFenceCounter = registry.counter("geofencing.mover.query.sent.failed.counter");
    }

    public void incrementSuccessfullySentQueryByFenceCounter() {
        successfullySentQueryByFenceCounter.increment();
    }
    public void incrementFailedQueryByFenceCounter() {
        failedQueryByFenceCounter.increment();
    }
}
