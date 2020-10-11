package statefull.geofencing.faas.realtime.fencing.query;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import statefull.geofencing.faas.realtime.fencing.dto.FenceDto;
import statefull.geofencing.faas.realtime.fencing.dto.FencesDto;
import statefull.geofencing.faas.realtime.fencing.exception.NotFoundException;

@RestController
@RequestMapping("/api/views/fences")
public class FenceViewResource {

    private FenceViewService service;

    public FenceViewResource(FenceViewService service) {
        this.service = service;
    }

    //isHighLevelQuery query param is related to inter instance communication and it should be true in normal operations or not defined
    @GetMapping
    public Mono<FencesDto> getFences(@RequestParam(required = false, value = ViewService.HIGH_LEVEL_QUERY_PARAM_NAME,
            defaultValue = "true") boolean isHighLevelQuery) {
        return service.getAll(isHighLevelQuery);
    }

    @GetMapping("/{id}")
    public Mono<FenceDto> getFenceById(@PathVariable("id") String id) {
        return service.getById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("%s not found (%s doesn't exist).", "Fence", id))));
    }
}