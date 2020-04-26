package wonderland.faas.stateful.geofencing.streamprocessing;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import wonderland.faas.stateful.geofencing.config.TopicCreator;
import wonderland.faas.stateful.geofencing.domain.Coordinate;
import wonderland.faas.stateful.geofencing.domain.Mover;
import wonderland.faas.stateful.geofencing.dto.MoverPositionUpdate;
import wonderland.faas.stateful.geofencing.repository.MoverRepository;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static wonderland.faas.stateful.geofencing.CustomSerdes.MOVER_JSON_SERDE;
import static wonderland.faas.stateful.geofencing.CustomSerdes.MOVER_POSITION_UPDATE_JSON_SERDE;
import static wonderland.faas.stateful.geofencing.config.Stores.MOVER_GLOBAL_STATE_STORE;
import static wonderland.faas.stateful.geofencing.config.Stores.MOVER_IN_MEMORY_STATE_STORE;
import static wonderland.faas.stateful.geofencing.config.Topics.MOVER_POSITION_UPDATES_TOPIC;

@Configuration
public class KStreamAndKTableDefinitions {

    private static final Consumed<String, MoverPositionUpdate> MOVER_POSITION_UPDATE_CONSUMED = Consumed.with(Serdes.String(), MOVER_POSITION_UPDATE_JSON_SERDE);
    private static final Consumed<String, Mover> MOVER_CONSUMED = Consumed.with(Serdes.String(), MOVER_JSON_SERDE);

    // Use an in-memory store for intermediate state storage.
    private static final Materialized<String, Mover, KeyValueStore<Bytes, byte[]>> IN_MEMORY_TEMP_KTABLE = Materialized
            .<String, Mover>as(Stores.inMemoryKeyValueStore(MOVER_IN_MEMORY_STATE_STORE))
            .withKeySerde(Serdes.String())
            .withValueSerde(MOVER_JSON_SERDE);

    private static final Logger LOGGER = LoggerFactory.getLogger(KStreamAndKTableDefinitions.class);

//    //this can be scaled independently
//    private BiFunction<MoverPositionUpdate, UpdateProducer, MoverPositionUpdate> updateProducer; //exposed as post a request
//
//    //these three should be scaled together all as one docker container
//    private BiPredicate<String, MoverPositionUpdate> filter; //not exposed but just used by framework
//    private TriFunction<String, MoverPositionUpdate, ? extends Mover, ? extends Mover> aggregate; //not exposed but just used by framework
//    private BiFunction<MoverRepository, Set<Coordinate>, Set<? extends Mover>> geoFence; //exposed as post a request

    private StreamsBuilder builder;
    private MoverRepository repository;
    private String applicationName;

    public KStreamAndKTableDefinitions(StreamsBuilder builder, MoverRepository repository, @Value("${spring.application.name}") String applicationName) {
        this.builder = builder;
        this.repository = repository;
        this.applicationName = applicationName;
    }

    @PostConstruct
    public void configureStores() {
        builder
                // create a stream from the provider updates topic
                .stream(MOVER_POSITION_UPDATES_TOPIC, MOVER_POSITION_UPDATE_CONSUMED)
                .filterNot((k, v) -> k == null || k.isBlank() || k.isEmpty() || v == null || v.getKey() == null || v.getKey().isEmpty() || v.getKey().isBlank())
                .filter((k, v) -> k.equals(v.getKey()))
                .filter((k, v) -> v.getLatitude() >= 0)
                .filter((k, v) -> v.getLongitude() >= 0)
//                .filter(filter::test)
                .peek((key, value) -> LOGGER.trace("Mover Position update {} -> {}", key, value))
                .groupByKey()
                // Aggregate status into a in-memory KTable as a source for global KTable
                .aggregate(() -> Mover.builder().build(), (key, positionUpdate, mover) ->
                                //availability is hard coded to true for now and for simplicity. The events (position update) doesn't support it yet.
                                Mover.define(key).updatedAvailability(true, Coordinate.of(positionUpdate.getLatitude(), positionUpdate.getLongitude())),
                        IN_MEMORY_TEMP_KTABLE);

        // register a global store which reads directly from the aggregated in memory table's changelog
        var storeBuilder = new MoverStore.Builder(MOVER_GLOBAL_STATE_STORE, Time.SYSTEM, repository);
        builder.addGlobalStore(storeBuilder,
                TopicCreator.storeTopicName(MOVER_IN_MEMORY_STATE_STORE, applicationName),
                MOVER_CONSUMED, () -> new MoverProcessor(MOVER_GLOBAL_STATE_STORE));
    }

}
