package statefull.geofencing.faas.realtime.fencing.resource;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import statefull.geofencing.faas.realtime.fencing.config.Topics;
import statefull.geofencing.faas.realtime.fencing.dto.FenceDto;
import statefull.geofencing.faas.realtime.fencing.dto.FencesDto;
import statefull.geofencing.faas.realtime.fencing.exception.IllegalInputException;
import statefull.geofencing.faas.realtime.fencing.exception.NotFoundException;
import statefull.geofencing.faas.realtime.fencing.service.FenceViewService;
import statefull.geofencing.faas.realtime.fencing.service.ViewService;

@RestController
@RequestMapping("/api/fences")
public class FenceResource {

    public final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);
    public final static WKTReader wktReader = new WKTReader(GEOMETRY_FACTORY);
    private final FenceViewService service;
    private final KafkaTemplate<String, String> fenceEventPublisher;

    public FenceResource(FenceViewService service, KafkaTemplate<String, String> fenceEventPublisher) {
        this.service = service;
        this.fenceEventPublisher = fenceEventPublisher;
    }

    //isHighLevelQuery query param is related to inter instance communication and it should be true in normal operations or not defined
    @GetMapping
    public Mono<FencesDto> getFences(@RequestParam(required = false, value = ViewService.HIGH_LEVEL_QUERY_PARAM_NAME,
            defaultValue = "true") boolean isHighLevelQuery) {
        return service.getAll(isHighLevelQuery);
    }

    @GetMapping("/{moverId}")
    public Mono<FenceDto> getFenceByMoverId(@PathVariable("moverId") String moverId) {
        return service.getById(moverId)
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("%s not found (%s doesn't exist).", "Fence for moverId", moverId))));
    }

    //todo support @Put for adding new polygons to the current fence for a mover (Post is for replacement)

    @PostMapping
    public Mono defineFenceForMover(@RequestBody FenceDto fenceDto) {
        try {
            Geometry fence = wktReader.read(fenceDto.getWkt());
//            if (!fence.isValid()){
//                throw new IllegalInputException("provided wkt is parsed into a non valid geometry");
//            }
        } catch (ParseException e) {
            throw new IllegalInputException("provided wkt is not a parsable: " + e.getMessage());
        }
        return Mono.fromFuture(fenceEventPublisher.send(Topics.FENCE_EVENT_LOG, fenceDto.getMoverId(), fenceDto.getWkt()).completable());
    }

    @DeleteMapping("/{moverId}")
    public Mono defineFenceForMover(@PathVariable("moverId") String moverId) {
        return Mono.fromFuture(fenceEventPublisher.send(Topics.FENCE_EVENT_LOG, moverId, null).completable());
    }
}