package statefull.geofencing.faas.bench.marking.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import statefull.geofencing.faas.bench.marking.client.model.FenceDto;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class LocationAggregateClient {

    private WebClient.Builder webClientBuilder;

    public LocationAggregateClient(@Qualifier("loadBalancedClient") WebClient.Builder loadBalancedWebClientBuilder,
                                   @Qualifier("notLoadBalancedClient") WebClient.Builder notLoadBalancedWebClientBuilder,
                                   Environment environment) {
        var activeProfiles = Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toSet());
        if (activeProfiles.contains("no-consul")){
            webClientBuilder = notLoadBalancedWebClientBuilder;
        }else{
            webClientBuilder = loadBalancedWebClientBuilder;
        }
    }

    public Mono<String> queryMoverLocationsByFence(String wkt) {
        return webClientBuilder
                .build()
                .post()
                .uri("http://locaiton-aggregate/api/movers/wkt")
                .bodyValue(wkt)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(this::handleClientError);
    }

    Mono handleClientError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException
                && ((WebClientResponseException) throwable).getStatusCode().is4xxClientError()) {
            return Mono.empty();
        } else {
            return Mono.error(throwable);
        }
    }
}