package statefull.geofencing.faas.realtime.fencing.streamprocessing;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.realtime.fencing.CustomSerdes;
import statefull.geofencing.faas.realtime.fencing.config.Stores;
import statefull.geofencing.faas.realtime.fencing.config.Topics;
import statefull.geofencing.faas.realtime.fencing.domain.Fence;
import statefull.geofencing.faas.realtime.fencing.dto.FenceDto;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.lang.Boolean.FALSE;

@Configuration
public class KStreamAndKTableDefinitions {

    private static final Logger LOGGER = LoggerFactory.getLogger(KStreamAndKTableDefinitions.class);
    private static final Consumed<String, Mover> MOVER_CONSUMED = Consumed.with(Serdes.String(), CustomSerdes.MOVER_JSON_SERDE);
    private static final Consumed<String, String> WKT_CONSUMED = Consumed.with(Serdes.String(), Serdes.String());
    public final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);
    public final static WKTReader wktReader = new WKTReader(GEOMETRY_FACTORY);

    private static final Materialized<String, Fence, KeyValueStore<Bytes, byte[]>> FENCE_KTABLE = Materialized
            .<String, Fence, KeyValueStore<Bytes, byte[]>>as(Stores.FENCE_STATE_STORE)
            .withKeySerde(Serdes.String())
            .withValueSerde(new JsonSerde<Fence>(Fence.class));

    private final StreamsBuilder streamsBuilder;

    public KStreamAndKTableDefinitions(StreamsBuilder streamsBuilder) {
        this.streamsBuilder = streamsBuilder;
    }
    BiFunction<Mover, Fence, KeyValue<String, Boolean>> moverFenceIntersectionChecker = (mover, fence) -> {
        try {
            System.out.println("** before intersection operation:"+ mover +":"+ fence);
            var point = GEOMETRY_FACTORY.createPoint(new Coordinate(mover.getLastLocation().getLatitude(),
                    mover.getLastLocation().getLongitude()));
            var fenceGeometry = wktReader.read(fence.getWkt());
            return KeyValue.pair(mover.getId(), fenceGeometry.intersects(point));
        } catch (Exception e) {
            LOGGER.error("error while intersecting {} with fence {}", mover, fence, e);
            return KeyValue.pair(mover.getId(), null);
        }
    };

    @PostConstruct
    public void configureStores() {
        var moversFenceKTable = streamsBuilder.stream(Topics.FENCE_EVENT_LOG, WKT_CONSUMED)
                .filterNot((key, value) -> key == null || key.isEmpty() || key.isBlank())
                .filterNot((key, value) -> value == null || value.isEmpty() || value.isBlank())
                .groupByKey()
                .aggregate(Fence::defineEmpty,
                        (moverId, newWkt, currentFence) -> Fence.define(newWkt, moverId),
                        FENCE_KTABLE);

        streamsBuilder.stream(Topics.MOVER_UPDATES_TOPIC, MOVER_CONSUMED)
                .filterNot((key, value) -> key == null || key.isEmpty() || key.isBlank())
                .filterNot((key, value) -> value.isNotDefined())
                .join(moversFenceKTable, moverFenceIntersectionChecker::apply)
                .groupByKey()
                .aggregate(() -> FALSE, (moverId, newFenceIntersectionStatus, currentFenceIntersectionStatus) -> currentFenceIntersectionStatus)
                .toStream()
                .foreach((moverId, intersects) -> System.out.println(moverId + " intersection status with its " +
                        "corresponding fence is: "+ intersects));
        //todo
        // check the intersection
//todo turn the join stream into another KTable of "mover is/is not in deifned fence" and produce "move
        // left/moved to the defined fence" events.
    }
}
