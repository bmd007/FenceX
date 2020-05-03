package statefull.geofencing.faas.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import java.time.Instant;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = MoverLocationUpdate.Builder.class)
public class MoverLocationUpdate {

    private final String moverId;
    private final Instant timestamp;
    private final double latitude;
    private final double longitude;

    @JsonIgnore
    public boolean isNotDefined(){
        return moverId==null || moverId.isEmpty() || moverId.isBlank();
    }

    private MoverLocationUpdate(Builder builder) {
        moverId = builder.moverId;
        timestamp = builder.timestamp;
        latitude = builder.latitude;
        longitude = builder.longitude;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(MoverLocationUpdate copy) {
        Builder builder = new Builder();
        builder.moverId = copy.getMoverId();
        builder.timestamp = copy.getTimestamp();
        builder.latitude = copy.getLatitude();
        builder.longitude = copy.getLongitude();
        return builder;
    }

    @JsonIgnore
    public String getKey() {
        return moverId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("moverId", moverId)
                .add("timestamp", timestamp)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoverLocationUpdate that = (MoverLocationUpdate) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(moverId, that.moverId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moverId, timestamp, latitude, longitude);
    }

    public String getMoverId() {
        return moverId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static final class Builder {
        private String moverId;
        private Instant timestamp;
        private double latitude;
        private double longitude;

        private Builder() {
        }

        public Builder withMoverId(String val) {
            moverId = val;
            return this;
        }

        public Builder withTimestamp(Instant val) {
            timestamp = val;
            return this;
        }

        public Builder withLatitude(double val) {
            latitude = val;
            return this;
        }

        public Builder withLongitude(double val) {
            longitude = val;
            return this;
        }

        public MoverLocationUpdate build() {
            return new MoverLocationUpdate(this);
        }
    }
}
