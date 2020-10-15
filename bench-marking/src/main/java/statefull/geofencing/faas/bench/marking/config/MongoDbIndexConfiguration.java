package statefull.geofencing.faas.bench.marking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import reactor.core.publisher.Flux;
import statefull.geofencing.faas.bench.marking.domain.TripDocument;

import java.util.List;

@Configuration
public class MongoDbIndexConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoDbIndexConfiguration.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;

    public MongoDbIndexConfiguration(
            ReactiveMongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeIndices() {
        List.of(TripDocument.class)
                .forEach(
                        documentClass -> {
                            logger.info("Ensuring MongoDb index for {}", documentClass);
                            ReactiveIndexOperations indexOps = mongoTemplate.indexOps(documentClass);
                            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
                            Flux.fromIterable(resolver.resolveIndexFor(documentClass))
                                    .flatMap(indexOps::ensureIndex)
                                    .subscribe();
                        });
    }
}
