package statefull.geofencing.faas.realtime.fencing.streamprocessing;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.realtime.fencing.CustomSerdes;
import statefull.geofencing.faas.realtime.fencing.config.Stores;
import statefull.geofencing.faas.realtime.fencing.config.Topics;

import javax.annotation.PostConstruct;

@Configuration
public class KStreamAndKTableDefinitions {

    private static final Logger LOGGER = LoggerFactory.getLogger(KStreamAndKTableDefinitions.class);
    private static final Consumed<String, Mover> MOVER_CONSUMED = Consumed.with(Serdes.String(), CustomSerdes.MOVER_JSON_SERDE);
    private static final Consumed<String, String> WKT_CONSUMED = Consumed.with(Serdes.String(), Serdes.String());
    public final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);
    public final static WKTReader wktReader = new WKTReader(GEOMETRY_FACTORY);

    private static final Materialized<String, String, KeyValueStore<Bytes, byte[]>> FENCE_KTABLE = Materialized
            .<String, String, KeyValueStore<Bytes, byte[]>>as(Stores.FENCE_STATE_STORE)
            .withKeySerde(Serdes.String())
            .withValueSerde(Serdes.String());

    private final StreamsBuilder streamsBuilder;

    public KStreamAndKTableDefinitions(StreamsBuilder streamsBuilder) {
        this.streamsBuilder = streamsBuilder;
    }


    @PostConstruct
    public void configureStores() {
        var moversFenceKTable = streamsBuilder.stream(Topics.FENCE_EVENT_LOG, WKT_CONSUMED)
                .filterNot((key, value) -> key == null || key.isEmpty() || key.isBlank())
                .groupByKey()
                .aggregate(() -> "", (moverId, todo, currentWkt) -> todo, FENCE_KTABLE);

        KStream<String, KeyValue<String, String>> moverFenceIntersectionStream = streamsBuilder.stream(Topics.MOVER_UPDATES_TOPIC, MOVER_CONSUMED)
                .filterNot((key, value) -> key == null || key.isEmpty() || key.isBlank())
                .join(moversFenceKTable, (moverLocationUpdate, fenceWkt) -> KeyValue.pair(moverLocationUpdate.getId(), fenceWkt));//todo check the intersection
//todo turn the join stream into another KTable of "mover is/is not in deifned fence" and produce "move
        // left/moved to the defined fence" events.
    }
}
