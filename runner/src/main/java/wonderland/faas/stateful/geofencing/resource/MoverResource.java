package wonderland.faas.stateful.geofencing.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import wonderland.faas.stateful.geofencing.domain.Availability;
import wonderland.faas.stateful.geofencing.domain.Coordinate;
import wonderland.faas.stateful.geofencing.domain.Mover;
import wonderland.faas.stateful.geofencing.dto.CoordinateDto;
import wonderland.faas.stateful.geofencing.dto.MoverDto;
import wonderland.faas.stateful.geofencing.dto.MoversDto;
import wonderland.faas.stateful.geofencing.dto.PolygonDto;
import wonderland.faas.stateful.geofencing.repository.MoverRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movers")
public class MoverResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoverResource.class);
    private MoverRepository repository;

    public MoverResource(MoverRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public MoverDto get(@PathVariable("id") String id) {
        return map(repository.get(id));
    }

    @GetMapping("/box")
    public MoversDto queryBox(
            @RequestParam(required = true) double boxStartLatitude,
            @RequestParam(required = true) double boxStartLongitude,
            @RequestParam(required = true) double boxEndLatitude,
            @RequestParam(required = true) double boxEndLongitude,
            @RequestParam(required = false) Long maxAge,
            @RequestParam(required = false) Set<Boolean> availability) {
        return queryPolygon(List.of(
                new Coordinate(boxStartLatitude, boxStartLongitude),
                new Coordinate(boxEndLatitude, boxStartLongitude),
                new Coordinate(boxEndLatitude, boxEndLongitude),
                new Coordinate(boxStartLatitude, boxEndLongitude)), maxAge, availability);
    }

    @PostMapping("/polygon")
    public MoversDto queryPolygon(
            @Valid @RequestBody PolygonDto polygon,
            @RequestParam(required = false) Long maxAge,
            @RequestParam(required = false) Set<Boolean> availability) {
        return queryPolygon(polygon.getPoints()
                .stream()
                .map(point -> new Coordinate(point.getLatitude(), point.getLongitude()))
                .collect(Collectors.toList()), maxAge, availability);
    }

    private MoversDto queryPolygon(List<Coordinate> points, Long maxAge, Set<Boolean> availability) {
        LOGGER.debug("Executing query. Status: {}, MaxAge: {}, Polygon: {}", availability, maxAge, points);
        var results = repository.query(points)
                .stream()
                .filter(mover -> {
                    var date = maxAge == null || mover.receivedUpdate(maxAge);
                    var status = statusFilter(availability, mover.getAvailability());
                    return date && status;
                })
                .map(this::map)
                .collect(Collectors.toList());
        return new MoversDto(results);
    }

    private boolean statusFilter(Set<Boolean> statuses, Availability availability) {
        return statuses == null ||
                statuses.isEmpty() ||
                statuses.contains(availability.isAvailable());
    }

    private MoverDto map(Mover v) {
        return new MoverDto(v.getId(), new CoordinateDto(v.getPosition().getLatitude(), v.getPosition().getLongitude()));
    }
}
