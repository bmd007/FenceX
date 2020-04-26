package statefull.geofencing.faas.function;

import org.locationtech.jts.geom.Polygon;
import statefull.geo.fencing.faas.commons.domain.Mover;
import statefull.geo.fencing.faas.commons.repository.MoverJdbcRepository;

import java.util.List;
import java.util.function.BiFunction;

public class PolygonalGeoFencingFunction implements BiFunction<MoverJdbcRepository, Polygon, List<Mover>> {

    @Override
    public List<Mover> apply(MoverJdbcRepository moverJdbcRepository, Polygon polygon) {
        //implement me. below is a straight forward example
        return moverJdbcRepository.query(polygon);
    }
}
