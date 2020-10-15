package statefull.geofencing.faas.bench.marking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import statefull.geofencing.faas.bench.marking.domain.TripDocument;

public interface TripDocumentRepository extends ReactiveMongoRepository<TripDocument, String> {
}
