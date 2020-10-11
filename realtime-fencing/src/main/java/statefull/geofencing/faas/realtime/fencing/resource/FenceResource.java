package statefull.geofencing.faas.realtime.fencing.resource;

import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.common.repository.MoverJdbcRepository;
import statefull.geofencing.faas.realtime.fencing.dto.CoordinateDto;
import statefull.geofencing.faas.realtime.fencing.dto.FenceDto;
import statefull.geofencing.faas.realtime.fencing.dto.MoverDto;
import statefull.geofencing.faas.realtime.fencing.dto.MoversDto;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mover")
//todo
public class FenceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenceResource.class);

    @GetMapping("/{moverId}/fence")
    public FenceDto get(@PathVariable("moverId") String moverId) {
        
    }

    @PostMapping("/{moverId}/fence/wkt")
    public FenceDto queryPolygon(@PathVariable("moverId") String moverId, @RequestBody String kwtString) throws ParseException {
        
    }

}
