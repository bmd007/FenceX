package statefull.geofencing.faas.realtime.fencing;

import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.realtime.fencing.serialization.JsonSerde;

public class CustomSerdes {
    public static final JsonSerde<Mover> MOVER_JSON_SERDE = new JsonSerde<>(Mover.class);
}
