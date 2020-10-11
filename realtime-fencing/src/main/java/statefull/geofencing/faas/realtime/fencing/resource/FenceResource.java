package statefull.geofencing.faas.realtime.fencing.resource;

import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import statefull.geofencing.faas.common.domain.Mover;
import statefull.geofencing.faas.common.repository.MoverJdbcRepository;
import statefull.geofencing.faas.realtime.fencing.dto.CoordinateDto;
import statefull.geofencing.faas.realtime.fencing.dto.MoverDto;
import statefull.geofencing.faas.realtime.fencing.dto.MoversDto;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fences")
//todo
public class FenceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenceResource.class);

//    @GetMapping("/{id}")
//    public MoverDto get(@PathVariable("id") String id) {
//        return map(repository.get(id));
//    }
//
//    @GetMapping("/box")
//    public MoversDto queryBox(@RequestParam(required = true) double latitude,
//                              @RequestParam(required = true) double longitude,
//                              @RequestParam(required = false) Long maxAge) {
//        return queryPolygon(wrapLocationByPolygonFunction.apply(latitude, longitude), maxAge);
//    }
//
//    @PostMapping("/polygon")
//    public MoversDto queryPolygon(@RequestBody Polygon polygon, @RequestParam(required = false) Long maxAge) {
//        LOGGER.debug("Executing query. MaxAge: {}, Polygon: {}", maxAge, polygon);
//        var results = polygonalGeoFencingFunction.apply(repository, polygon)
//                .stream()
//                .map(this::map)
//                .collect(Collectors.toList());
//        return new MoversDto(results);
//    }
//
//    @PostMapping("/kwt")
//    public MoversDto queryPolygon(@RequestBody String kwtString, @RequestParam(required = false) Long maxAge) throws ParseException {
//        LOGGER.debug("Executing query. MaxAge: {}, Polygon: {}", maxAge, kwtString);
//        var polygon = (Polygon) repository.getWktReader().read(kwtString);
//        var results = repository.query(polygon)
//                .stream()
//                .map(this::map)
//                .collect(Collectors.toList());
//        return new MoversDto(results);
//    }

}
