package statefull.geofencing.faas.location.update.publisher.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import statefull.geofencing.faas.common.dto.MoverLocationUpdate;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = TimeLessMoverLocationUpdate.Builder.class)
public class TimeLessMoverLocationUpdate {

    private final String moverId;
    private final double latitude;
    private final double longitude;

    private TimeLessMoverLocationUpdate(Builder builder) {
        moverId = builder.moverId;
        latitude = builder.latitude;
        longitude = builder.longitude;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(MoverLocationUpdate copy) {
        Builder builder = new Builder();
        builder.moverId = copy.getMoverId();
        builder.latitude = copy.getLatitude();
        builder.longitude = copy.getLongitude();
        return builder;
    }

    @JsonIgnore
    public boolean isNotDefined() {
        return moverId == null || moverId.isEmpty() || moverId.isBlank();
    }

    @JsonIgnore
    public String getKey() {
        return moverId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("moverId", moverId)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (TimeLessMoverLocationUpdate) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(moverId, that.moverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moverId, latitude, longitude);
    }

    public String getMoverId() {
        return moverId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static final class Builder {
        private String moverId;
        private double latitude;
        private double longitude;

        private Builder() {
        }

        public Builder withMoverId(String val) {
            moverId = val;
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

        public TimeLessMoverLocationUpdate build() {
            return new TimeLessMoverLocationUpdate(this);
        }
    }
}
