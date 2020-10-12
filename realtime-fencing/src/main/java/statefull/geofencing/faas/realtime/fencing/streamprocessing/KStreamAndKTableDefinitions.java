package statefull.geofencing.faas.realtime.fencing.streamprocessing;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import statefull.geofencing.faas.realtime.fencing.CustomSerdes;

import javax.annotation.PostConstruct;

@Configuration
public class KStreamAndKTableDefinitions {

    private static final Consumed<String, Mover> MOVER_CONSUMED = Consumed.with(Serdes.String(), CustomSerdes.MOVER_JSON_SERDE);

    private static final Logger LOGGER = LoggerFactory.getLogger(KStreamAndKTableDefinitions.class);

//    private final Predicate<MoverLocationUpdate> moverLocationUpdateFilterFunction;
//    private final BiFunction<Mover, MoverLocationUpdate, Mover> moverAggregateFunction;
    private final StreamsBuilder builder;

    public KStreamAndKTableDefinitions(StreamsBuilder builder) {
        this.builder = builder;
    }
//    private final String applicationName;

    @PostConstruct
    public void configureStores() {


//        builder
//                .stream(Topics.MOVER_UPDATES_TOPIC, MOVER_CONSUMED)
//                .filter((k, v) -> k.equals(v.getId()))
//                .join()
     }
//
//     //todo join mover update stream with fenceKtable

}
