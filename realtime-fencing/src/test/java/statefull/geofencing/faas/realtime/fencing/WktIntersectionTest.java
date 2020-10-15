package statefull.geofencing.faas.realtime.fencing;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.GeometryCombiner;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@ActiveProfiles("test")
//@EmbeddedKafka(partitions = 1, topics = {
//        "mover-updates",
//        "${spring.application.name}"+"-changelog-"+ Stores.FENCE_STATE_STORE
//})
public class WktIntersectionTest {

    public final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);
    public final static WKTReader wktReader = new WKTReader(GEOMETRY_FACTORY);
    public final static WKTWriter wktWriter = new WKTWriter();

    @Test
    public void testMultiPolygonIntersectionWithPoint() throws ParseException, JsonProcessingException {
        //given
        var point = (Point) wktReader.read("POINT(28.17382723093031 20.352627225617212)");
        var lineString = (LineString) wktReader.read("LINESTRING(25.522459596395475 27.762399712625896,30.444334596395475 30.676755226021278,36.33300647139547 26.0381281853519,25.522459596395475 27.762399712625896)");
        var polygon1wkt = "POLYGON((23.22265446186064 21.978131144025294,29.81445133686064 24.04087977989185,36.757810711860635 21.733411220672618,40.185545086860635 11.043036865006519,22.87109196186064 9.919542613262635,23.22265446186064 21.978131144025294))";
        var polygon1 = (Polygon) wktReader.read(polygon1wkt);
        var polygon2wkt = "POLYGON((16.118162721395475 29.229945094608624,18.754881471395475 25.563355673548784,15.327147096395475 22.107118925668917,11.899412721395475 24.608168342021944,16.118162721395475 29.229945094608624))";
        var polygon2 = (Polygon) wktReader.read(polygon2wkt);
        var listOfPolygons = List.of(polygon1, polygon2.getBoundary());
        var combined = GeometryCombiner.combine(listOfPolygons);

        var tempGeo = (GeometryCollection) wktReader.read(combined.toString());
        System.out.println(combined);
        System.out.println(tempGeo);

        //when
        var result = combined.intersects(point);
        //then
        assertTrue(result);
        assertTrue(wktReader.read(polygon1wkt).intersects(point));
        assertTrue(polygon1.getBoundary().intersects(polygon1.getBoundary().getInteriorPoint()));
    }
}
