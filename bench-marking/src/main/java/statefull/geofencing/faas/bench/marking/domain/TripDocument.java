package statefull.geofencing.faas.bench.marking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(builder = TripDocument.Builder.class)
@Document("tripDocument")
public class TripDocument {

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);

    @Id
    private final String tripId;
    private final List<LocationReport> locationReports;
    private final String routeWkt;
    private final String middleRouteRingWkt;

    @PersistenceConstructor
    public TripDocument(String tripId, List<LocationReport> locationReports, String routeWkt, String middleRouteRingWkt) {
        this.tripId = tripId;
        this.routeWkt = routeWkt;
        this.middleRouteRingWkt = middleRouteRingWkt;
        this.locationReports = locationReports;
    }

    private TripDocument(Builder builder) {
        tripId = builder.tripId;
        locationReports = builder.locationReports;
        routeWkt = builder.routeWkt;
        middleRouteRingWkt = builder.middleRouteRingWkt;
    }

    @JsonIgnore
    public Mono<TripDocument> populateWktRoute(){
        return Flux.fromIterable(locationReports)
                .map(report -> new Coordinate(report.getLongitude(), report.getLatitude()))
                .collectList()
                .map(coordinates -> {
                    var array = new Coordinate[coordinates.size()];
                    coordinates.toArray(array);
                    return array;
                })
                .map(GEOMETRY_FACTORY::createLineString)
                .map(Geometry::toText)
                .map(wkt -> cloneBuilder().withRouteWkt(wkt).build());
    }

    @JsonIgnore
    public TripDocument populateMiddleRouteRingWkt(){
        //todo throw error on empty list
        var middleOfLocationReportsList = (Integer) locationReports.size()/2;
        var middleOfRoutePoint = locationReports.get(middleOfLocationReportsList);
        var coordinate = new Coordinate(middleOfRoutePoint.getLongitude(), middleOfRoutePoint.getLatitude());
        var ringInTheMiddleOfRoute = GEOMETRY_FACTORY.createPoint(coordinate).buffer(0.0005);
        var ringWkt = ringInTheMiddleOfRoute.toString();
        return cloneBuilder().withMiddleRouteRingWkt(ringWkt).build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TripDocument copy) {
        Builder builder = new Builder();
        builder.tripId = copy.getTripId();
        builder.locationReports = copy.getLocationReports();
        builder.routeWkt = copy.getRouteWkt();
        builder.middleRouteRingWkt = copy.getMiddleRouteRingWkt();
        return builder;
    }

    public Builder cloneBuilder() {
        return newBuilder(this);
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteWkt() {
        return routeWkt;
    }

    public String getMiddleRouteRingWkt() {
        return middleRouteRingWkt;
    }

    public List<LocationReport> getLocationReports() {
        return locationReports;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tripId", tripId)
                .add("routeWkt", routeWkt)
                .add("middleRouteRingWkt", middleRouteRingWkt)
                .add("locationReports", locationReports)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripDocument that = (TripDocument) o;
        return Objects.equal(tripId, that.tripId) &&
                Objects.equal(routeWkt, that.routeWkt) &&
                Objects.equal(middleRouteRingWkt, that.middleRouteRingWkt) &&
                Objects.equal(locationReports, that.locationReports);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tripId, locationReports, routeWkt, middleRouteRingWkt);
    }

    public static final class Builder {
        private String tripId = null;
        private String routeWkt = null;
        private String middleRouteRingWkt = null;
        private List<LocationReport> locationReports = new ArrayList<>();

        private Builder() {
        }

        public Builder withTripId(String val) {
            tripId = val;
            return this;
        }

        public Builder withRouteWkt(String val) {
            routeWkt = val;
            return this;
        }
        public Builder withMiddleRouteRingWkt(String val) {
            middleRouteRingWkt = val;
            return this;
        }

        public Builder withLocationReports(List<LocationReport> val) {
            locationReports = val;
            return this;
        }

        public TripDocument build() {
            return new TripDocument(this);
        }
    }
}