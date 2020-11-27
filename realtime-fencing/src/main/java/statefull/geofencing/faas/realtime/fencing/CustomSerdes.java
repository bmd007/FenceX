package statefull.geofencing.faas.realtime.fencing;

import statefull.geofencing.faas.common.dto.MoverLocationUpdate;
import statefull.geofencing.faas.realtime.fencing.serialization.JsonSerde;

public class CustomSerdes {
    public static final JsonSerde<MoverLocationUpdate> MOVER_POSITION_UPDATE_JSON_SERDE = new JsonSerde<>(MoverLocationUpdate.class);
}
