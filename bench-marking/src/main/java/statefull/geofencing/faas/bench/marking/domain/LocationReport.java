package statefull.geofencing.faas.bench.marking.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;

@JsonDeserialize(builder = LocationReport.Builder.class)
public class LocationReport {
    private Instant timestamp;
    private double latitude;
    private double longitude;

    final static DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(
            "UTC"));
    @PersistenceConstructor
    public LocationReport(Instant timestamp, double latitude, double longitude) {
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static LocationReport define(String timestamp, double latitude, double longitude) {
        var dateTime = LocalDateTime.parse(timestamp, DATE_FORMATTER);
        return newBuilder()
                .withLatitude(latitude)
                .withLongitude(longitude)
                .withTimestamp(dateTime.toInstant(ZoneOffset.UTC))
                .build();
    }

    private LocationReport(Builder builder) {
        timestamp = builder.timestamp;
        latitude = builder.latitude;
        longitude = builder.longitude;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationReport that = (LocationReport) o;
        return  Objects.equal(timestamp, that.timestamp) &&
                Objects.equal(latitude, that.latitude) &&
                Objects.equal(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timestamp, latitude, longitude);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .toString();
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

    public static Builder newBuilder(LocationReport copy) {
        Builder builder = new Builder();
        builder.timestamp = copy.getTimestamp();
        builder.latitude = copy.getLatitude();
        builder.longitude = copy.getLongitude();
        return builder;
    }

    public static final class Builder {
        private Instant timestamp;
        private double latitude;
        private double longitude;

        private Builder() {
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

        public LocationReport build() {
            return new LocationReport(this);
        }
    }
}
