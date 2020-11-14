package statefull.geofencing.faas.bench.marking.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import statefull.geofencing.faas.bench.marking.client.model.MoverLocationUpdate;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class LocationUpdatePublisherClient {

    private WebClient.Builder webClientBuilder;

    public LocationUpdatePublisherClient(@Qualifier("loadBalancedClient") WebClient.Builder loadBalancedWebClientBuilder,
                               @Qualifier("notLoadBalancedClient") WebClient.Builder notLoadBalancedWebClientBuilder,
                               Environment environment) {
        var activeProfiles = Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toSet());
        if (activeProfiles.contains("no-consul")){
            webClientBuilder = notLoadBalancedWebClientBuilder;
        }else{
            webClientBuilder = loadBalancedWebClientBuilder;
        }
    }

    public Mono<Void> requestLocationUpdate(MoverLocationUpdate moverLocationUpdate) {
        return webClientBuilder
                .build()
                .post()
                .uri("http://location-update-publisher/api/location/update")
                .bodyValue(moverLocationUpdate)
                .retrieve()
                .bodyToMono(Void.class)
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