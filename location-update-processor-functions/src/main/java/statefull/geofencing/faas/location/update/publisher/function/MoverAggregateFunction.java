package statefull.geofencing.faas.location.update.publisher.function;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;
import statefull.geofencing.faas.common.domain.Coordinate;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;

import java.util.function.BiFunction;

@Component
public class MoverAggregateFunction implements BiFunction<Mover, MoverLocationUpdate, Mover> {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);

    @Override
    public Mover apply(Mover currentState, MoverLocationUpdate locationUpdate) {
        //implement me. below is an straight forward example
        if (!locationUpdate.isNotDefined()) {

            if (!currentState.isNotDefined() && !currentState.getId().equals(locationUpdate.getMoverId())) {
                //todo throw error: obsessive check because of key = id
            }

            return Mover.newBuilder()
                    .withUpdatedAt(locationUpdate.getTimestamp())
                    .withLastLocation(new Coordinate(locationUpdate.getLatitude(), locationUpdate.getLongitude()))
                    .withId(locationUpdate.getMoverId())
                    .build();
        }

        return null;
    }
}
