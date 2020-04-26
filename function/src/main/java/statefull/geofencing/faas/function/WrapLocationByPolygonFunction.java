package statefull.geofencing.faas.function;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.function.BiFunction;

public class WrapLocationByPolygonFunction implements BiFunction<Double, Double, Polygon> {

    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue),4326);

    @Override
    public Polygon apply(Double latitude, Double longitude) {
        //implement me below is an example
        var point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        return (Polygon) point.buffer(1000);
    }
}
