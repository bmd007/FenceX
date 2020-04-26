package wonderland.faas.stateful.geofencing;

import wonderland.faas.stateful.geofencing.domain.Mover;
import wonderland.faas.stateful.geofencing.dto.MoverPositionUpdate;
import wonderland.faas.stateful.geofencing.serialization.JsonSerde;

public class CustomSerdes {
    public static final JsonSerde<Mover> MOVER_JSON_SERDE = new JsonSerde<>(Mover.class);
    public static final JsonSerde<MoverPositionUpdate> MOVER_POSITION_UPDATE_JSON_SERDE = new JsonSerde<>(MoverPositionUpdate.class);
}
