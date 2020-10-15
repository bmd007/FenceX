package statefull.geofencing.faas.bench.marking.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@JsonDeserialize(builder = TripDataDto.Builder.class)
public class TripDataDto {
    private String tripRefNumber;
    private String timestamp;
    private double latitude;
    private double longitude;

    private TripDataDto(Builder builder) {
        tripRefNumber = builder.tripRefNumber;
        timestamp = builder.timestamp;
        latitude = builder.latitude;
        longitude = builder.longitude;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TripDataDto copy) {
        Builder builder = new Builder();
        builder.tripRefNumber = copy.getTripRefNumber();
        builder.timestamp = copy.getTimestamp();
        builder.latitude = copy.getLatitude();
        builder.longitude = copy.getLongitude();
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripDataDto that = (TripDataDto) o;
        return Objects.equal(tripRefNumber, that.tripRefNumber) &&
                Objects.equal(timestamp, that.timestamp) &&
                Objects.equal(latitude, that.latitude) &&
                Objects.equal(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tripRefNumber, timestamp, latitude, longitude);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tripRefNumber", tripRefNumber)
                .add("timestamp", timestamp)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .toString();
    }

    public String getTripRefNumber() {
        return tripRefNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static final class Builder {
        private String tripRefNumber;
        private String timestamp;
        private double latitude;
        private double longitude;

        private Builder() {
        }

        public Builder withTripRefNumber(String val) {
            tripRefNumber = val;
            return this;
        }

        public Builder withTimestamp(String val) {
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

        public TripDataDto build() {
            return new TripDataDto(this);
        }
    }
}
