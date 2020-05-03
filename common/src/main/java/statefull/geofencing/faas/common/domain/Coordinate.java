package statefull.geofencing.faas.common.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import java.util.Objects;

@JsonDeserialize(builder = Coordinate.Builder.class)
public class Coordinate {

    private final double latitude;
    private final double longitude;

    private Coordinate(Builder builder) {
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Coordinate nullValue() {
        return new Coordinate(0, 0);
    }

    public static Coordinate of(double latitude, double longitude) {
        return new Coordinate(latitude, longitude);
    }

    public static Builder builder() {
        return new Builder();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Coordinate)) {
            return false;
        }
        Coordinate castOther = (Coordinate) other;
        return Objects.equals(latitude, castOther.latitude) && Objects.equals(longitude, castOther.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("latitude", latitude).add("longitude", longitude).toString();
    }

    public static final class Builder {

        private double latitude;
        private double longitude;

        private Builder() {
        }

        public Builder withLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder withLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Coordinate build() {
            return new Coordinate(this);
        }
    }

}
