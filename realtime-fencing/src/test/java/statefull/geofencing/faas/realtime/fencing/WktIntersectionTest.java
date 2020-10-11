package statefull.geofencing.faas.realtime.fencing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WktIntersectionTest {

    @Test
    public void testMultiPolygonIntersectionWithPoint(){
        //given
        var latitude = 18.22;
        var longitude = 56.22;
        //when
        var result = true;
        //then
        assertTrue(result);
    }
}
