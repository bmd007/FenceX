package statefull.geofencing.faas.function;

import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.common.repository.MoverJdbcRepository;

import java.util.List;
import java.util.function.BiFunction;

@Component
public class PolygonalGeoFencingFunction implements BiFunction<MoverJdbcRepository, Polygon, List<Mover>> {

    @Override
    public List<Mover> apply(MoverJdbcRepository moverJdbcRepository, Polygon polygon) {
        //implement me. below is a straight forward example
        return moverJdbcRepository.query(polygon);
    }
}
