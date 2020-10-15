package statefull.geofencing.faas.bench.marking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.locationtech.jts.geom.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@JsonDeserialize(builder = TripDocument.Builder.class)
@Document("tripDocument")
public class TripDocument {

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);

    @Id private String tripId;
    private List<LocationReport> locationReports = List.of();
    private String routeWkt;

    @PersistenceConstructor
    public TripDocument(String tripId, List<LocationReport> locationReports, String routeWkt) {
        this.tripId = tripId;
        this.routeWkt = routeWkt;
        this.locationReports = locationReports;
    }

    public static TripDocument define(String tripId){
        return TripDocument.newBuilder()
                .withTripId(tripId)
                .withRouteWkt("EMPTY")
                .build();
    }

    @JsonIgnore
    public TripDocument populateWktRoute(){
        return locationReports.stream()
                .map(report -> new Coordinate(report.getLatitude(), report.getLongitude()))
                .collect(toList())

                .map(coordinates -> {
                    var array = new Coordinate[coordinates.size()];
                    coordinates.toArray(array);
                    return array;
                })
                .map(GEOMETRY_FACTORY::createLineString)
                .map(lineString -> lineString.toText())
                .map(wkt -> cloneBuilder().withRouteWkt(wkt).build());
    }

    private TripDocument(Builder builder) {
        tripId = builder.tripId;
        locationReports = builder.locationReports;
        routeWkt = builder.routeWkt;
    }

    public Builder cloneBuilder(){
        return newBuilder(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TripDocument copy) {
        Builder builder = new Builder();
        builder.tripId = copy.getTripId();
        builder.locationReports = copy.getLocationReports();
        builder.routeWkt = copy.getRouteWkt();
        return builder;
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteWkt() {
        return routeWkt;
    }

    public List<LocationReport> getLocationReports() {
        return locationReports;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tripId", tripId)
                .add("routeWkt", routeWkt)
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
                Objects.equal(locationReports, that.locationReports);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tripId, locationReports, routeWkt);
    }

    public static final class Builder {
        private String tripId;
        private String routeWkt;
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

        public Builder withLocationReports(List<LocationReport> val) {
            locationReports = val;
            return this;
        }

        public TripDocument build() {
            return new TripDocument(this);
        }
    }
}