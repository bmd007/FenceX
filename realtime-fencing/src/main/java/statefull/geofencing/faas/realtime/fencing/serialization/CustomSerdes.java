package statefull.geofencing.faas.realtime.fencing.serialization;

import statefull.geofencing.faas.common.dto.MoverLocationUpdate;

public class CustomSerdes {
    public static final JsonSerde<MoverLocationUpdate> MOVER_POSITION_UPDATE_JSON_SERDE = new JsonSerde<>(MoverLocationUpdate.class);
}
