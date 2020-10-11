package statefull.geofencing.faas.realtime.fencing.query;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import statefull.geofencing.faas.realtime.fencing.config.Stores;
import statefull.geofencing.faas.realtime.fencing.domain.Fence;
import statefull.geofencing.faas.realtime.fencing.dto.FenceDto;
import statefull.geofencing.faas.realtime.fencing.dto.FencesDto;

import java.util.List;
import java.util.function.Function;

@Service
public class FenceViewService extends ViewService<FencesDto, FenceDto, Fence> {

    final static Function<FencesDto, List<FenceDto>> LIST_EXTRACTOR = FencesDto::getFences;
    final static Function<List<FenceDto>, FencesDto> LIST_WRAPPER = FencesDto::fences;
    final static Function<Fence, FenceDto> DTO_MAPPER = fence -> FenceDto.newBuilder().withMoverId(fence.getMoverId()).withWkt(fence.getWkt()).build();

    public FenceViewService(StreamsBuilderFactoryBean streams,
                            @Value("${kafka.streams.server.config.app-ip}") String ip,
                            @Value("${kafka.streams.server.config.app-port}") int port,
                            ViewResourcesClient commonClient) {
        super(ip, port, streams, Stores.FENCE_STATE_STORE,
                FencesDto.class, FenceDto.class,
                DTO_MAPPER, LIST_EXTRACTOR, LIST_WRAPPER,
                "fences", commonClient);
    }
}
