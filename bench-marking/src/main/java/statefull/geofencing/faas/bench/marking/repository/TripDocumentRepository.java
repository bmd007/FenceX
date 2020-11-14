package statefull.geofencing.faas.bench.marking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import statefull.geofencing.faas.bench.marking.domain.TripDocument;

@Repository
public interface TripDocumentRepository extends ReactiveMongoRepository<TripDocument, String> { }
