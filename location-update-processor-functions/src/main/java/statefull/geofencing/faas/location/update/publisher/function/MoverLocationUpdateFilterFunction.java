package statefull.geofencing.faas.location.update.publisher.function;

import org.springframework.stereotype.Component;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;

import java.util.function.Predicate;

@Component
public class MoverLocationUpdateFilterFunction implements Predicate<MoverLocationUpdate> {

    @Override
    public boolean test(MoverLocationUpdate moverLocationUpdate) {
        return true;  //implement me
    }
}
