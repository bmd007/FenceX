package statefull.geofencing.faas.function;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;

import java.util.UUID;
import java.util.function.BiFunction;

@Component
public class MoverAggregateFunction implements BiFunction<Mover, MoverLocationUpdate, Mover> {

    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue),4326);

    @Override
    public Mover apply(Mover currentState, MoverLocationUpdate locationUpdate) {
        //implement me. below is an straight forward example
        if (!currentState.getId().equals(locationUpdate.getMoverId())){
            //todo throw error
        }
        if (currentState==null){
            //todo check if possible what does it mean
        }

        var point = geometryFactory.createPoint(new Coordinate(locationUpdate.getLatitude(), locationUpdate.getLongitude()));
        var newStateBuilder = currentState.cloneBuilder()
                .withUpdatedAt(locationUpdate.getTimestamp())
                .withLastLocation(point);

        if (currentState.getId()==null || currentState.getId().isEmpty() || currentState.getId().isBlank()){
            newStateBuilder.withId(UUID.randomUUID().toString());
        }

        return newStateBuilder.build();
    }
}
