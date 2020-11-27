package statefull.geofencing.faas.realtime.fencing.streamprocessing;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;
import statefull.geofencing.faas.realtime.fencing.config.MetricsFacade;
import statefull.geofencing.faas.realtime.fencing.config.Stores;
import statefull.geofencing.faas.realtime.fencing.config.Topics;
import statefull.geofencing.faas.realtime.fencing.domain.Fence;
import statefull.geofencing.faas.realtime.fencing.domain.FenceIntersectionStatus;
import statefull.geofencing.faas.realtime.fencing.serialization.CustomSerdes;

import javax.annotation.PostConstruct;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Configuration
public class KStreamAndKTableDefinitions {

    public final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);
    public final static WKTReader WKT_READER = new WKTReader(GEOMETRY_FACTORY);
    private static final Logger LOGGER = LoggerFactory.getLogger(KStreamAndKTableDefinitions.class);

    private static final Consumed<String, MoverLocationUpdate> MOVER_POSITION_UPDATE_CONSUMED = Consumed.with(Serdes.String(), CustomSerdes.MOVER_POSITION_UPDATE_JSON_SERDE);
    private static final Consumed<String, String> WKT_CONSUMED = Consumed.with(Serdes.String(), Serdes.String());
    private static final Materialized<String, Fence, KeyValueStore<Bytes, byte[]>> FENCE_KTABLE = Materialized
            .<String, Fence, KeyValueStore<Bytes, byte[]>>as(Stores.FENCE_STATE_STORE)
            .withKeySerde(Serdes.String())
            .withValueSerde(new JsonSerde<Fence>(Fence.class));

    private final static BiFunction<MoverLocationUpdate, Fence, KeyValue<String, FenceIntersectionStatus>> moverFenceIntersectionChecker =
            (moverLocationUpdate, fence) -> {
                try {
                    var point = GEOMETRY_FACTORY.createPoint(new Coordinate(moverLocationUpdate.getLatitude(), moverLocationUpdate.getLongitude()));
                    var fenceGeometry = WKT_READER.read(fence.getWkt());
                    var intersects = fenceGeometry.intersects(point);
                    var intersectionStatus = FenceIntersectionStatus.define(intersects, moverLocationUpdate.getMoverId());
                    return KeyValue.pair(moverLocationUpdate.getMoverId(), intersectionStatus);
                } catch (Exception e) {
                    LOGGER.error("error while intersecting {} with fence {}", moverLocationUpdate, fence, e);
                    return KeyValue.pair(moverLocationUpdate.getMoverId(), null);
                }
            };

    private final Predicate<MoverLocationUpdate> moverLocationUpdateFilterFunction;
    private final StreamsBuilder streamsBuilder;
    private final MetricsFacade metrics;

    public KStreamAndKTableDefinitions(Predicate<MoverLocationUpdate> moverLocationUpdateFilterFunction, StreamsBuilder streamsBuilder, MetricsFacade metrics) {
        this.moverLocationUpdateFilterFunction = moverLocationUpdateFilterFunction;
        this.streamsBuilder = streamsBuilder;
        this.metrics = metrics;
    }

    @PostConstruct
    public void configureStores() {
        var moversFenceKTable = streamsBuilder.stream(Topics.FENCE_EVENT_LOG, WKT_CONSUMED)
                .filterNot((key, value) -> key == null || key.isEmpty() || key.isBlank())
                .filterNot((key, value) -> value == null || value.isEmpty() || value.isBlank())
                .groupByKey()
                .aggregate(Fence::defineEmpty,
                        (moverId, newWkt, currentFence) -> Fence.define(newWkt, moverId),
                        FENCE_KTABLE);

        streamsBuilder
                .stream(Topics.MOVER_POSITION_UPDATES_TOPIC, MOVER_POSITION_UPDATE_CONSUMED)
                .filterNot((k, v) -> k == null || k.isBlank() || k.isEmpty() || v == null || v.getKey() == null || v.getKey().isEmpty() || v.getKey().isBlank())
                .filter((k, v) -> k.equals(v.getKey()))
                .filter((k, v) -> v.getLatitude() >= -90 && v.getLatitude() <= 90)
                .filter((k, v) -> v.getLongitude() >= -180 && v.getLongitude() <= 180)
                .filterNot((key, value) -> key == null || key.isEmpty() || key.isBlank())
                .filterNot((key, value) -> value.isNotDefined())
                .filter((key, value) -> moverLocationUpdateFilterFunction.test(value))
                .join(moversFenceKTable, (mover, fence) -> moverFenceIntersectionChecker
                        .andThen(dontTouch -> {
                            metrics.incrementMoverFenceIntersectionCounter();
                            return dontTouch;
                        }).apply(mover, fence))
                .foreach((moverId, intersects) -> System.out.println(intersects));//todo integrate alarming function
        // here.
    }
}
