package statefull.geofencing.faas.bench.marking.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(builder = TripDocument.Builder.class)
@Document("tripDocument")
public class TripDocument {

    @Id private String tripId;
    private List<LocationReport> locationReports = List.of();

    @PersistenceConstructor
    public TripDocument(String tripId, List<LocationReport> locationReports) {
        this.tripId = tripId;
        this.locationReports = locationReports;
    }

    public static TripDocument define(String tripId){
        return TripDocument.newBuilder()
                .withTripId(tripId)
                .build();
    }

    private TripDocument(Builder builder) {
        tripId = builder.tripId;
        locationReports = builder.locationReports;
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
        return builder;
    }

    public String getTripId() {
        return tripId;
    }

    public List<LocationReport> getLocationReports() {
        return locationReports;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tripId", tripId)
                .add("locationReports", locationReports)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripDocument that = (TripDocument) o;
        return Objects.equal(tripId, that.tripId) &&
                Objects.equal(locationReports, that.locationReports);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tripId, locationReports);
    }

    public static final class Builder {
        private String tripId;
        private List<LocationReport> locationReports = new ArrayList<>();

        private Builder() {
        }

        public Builder withTripId(String val) {
            tripId = val;
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