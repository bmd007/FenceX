package statefull.geofencing.faas.realtime.fencing.streamprocessing;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;
import statefull.geofencing.faas.realtime.fencing.CustomSerdes;

import javax.annotation.PostConstruct;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Configuration
public class KStreamAndKTableDefinitions {

    private static final Consumed<String, Mover> MOVER_CONSUMED = Consumed.with(Serdes.String(), CustomSerdes.MOVER_JSON_SERDE);

    private static final Logger LOGGER = LoggerFactory.getLogger(KStreamAndKTableDefinitions.class);

//    private final Predicate<MoverLocationUpdate> moverLocationUpdateFilterFunction;
//    private final BiFunction<Mover, MoverLocationUpdate, Mover> moverAggregateFunction;
//    private final StreamsBuilder builder;
//    private final String applicationName;
//
//    public KStreamAndKTableDefinitions(Predicate<MoverLocationUpdate> moverLocationUpdateFilterFunction,
//                                       BiFunction<Mover, MoverLocationUpdate, Mover> moverAggregateFunction,
//                                       StreamsBuilder builder,
//                                       @Value("${spring.application.name}") String applicationName) {
//        this.moverLocationUpdateFilterFunction = moverLocationUpdateFilterFunction;
//        this.moverAggregateFunction = moverAggregateFunction;
//        this.builder = builder;
//        this.applicationName = applicationName;
//    }
//
//    @PostConstruct
//    public void configureStores() {
////        builder
////                // create a stream from the provider updates topic
////                .stream(Topics.MOVER_POSITION_UPDATES_TOPIC, MOVER_POSITION_UPDATE_CONSUMED)
////                .filterNot((k, v) -> k == null || k.isBlank() || k.isEmpty() || v == null || v.getKey() == null || v.getKey().isEmpty() || v.getKey().isBlank())
////                .filter((k, v) -> k.equals(v.getKey()))
////                .filter((k, v) -> v.getLatitude() >= -90 && v.getLatitude() <= 90)
////                .filter((k, v) -> v.getLongitude() >= -180 && v.getLongitude() <= 180)
////                .filter((key, value) -> moverLocationUpdateFilterFunction.test(value))
////                .groupByKey()
////                // Aggregate status into a in-memory KTable as a source for global KTable
////                .aggregate(Mover::defineEmpty, (key, value, aggregate) -> moverAggregateFunction.apply(aggregate, value),
////                        IN_MEMORY_TEMP_KTABLE)
//     }
//
//     //todo join mover update stream with fenceKtable

}
