package statefull.geofencing.faas.function;

import statefull.geo.fencing.faas.commons.dto.MoverLocationUpdate;

import java.util.function.Predicate;

public class MoverLocationUpdateFilterFunction implements Predicate<MoverLocationUpdate> {

    @Override
    public boolean test(MoverLocationUpdate moverLocationUpdate) {
        return true;  //implement me
    }
}
