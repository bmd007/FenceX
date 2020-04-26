package wonderland.faas.stateful.geofencing.domain;

import java.time.Instant;
import java.util.Objects;

public class Availability {

    private Instant timestamp;
    private Boolean available;

    private Availability(Builder builder) {
        this.timestamp = builder.timestamp;
        this.available = builder.status;
    }

    public Availability(Instant timestamp, Boolean available) {
        this.timestamp = timestamp;
        this.available = available;
    }

    public static Availability nullValue() {
        return new Availability(Instant.EPOCH, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Boolean getAvailable() {
        return available;
    }

    public Boolean isAvailable() {
        return available;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Availability)) {
            return false;
        }
        Availability castOther = (Availability) other;
        return Objects.equals(timestamp, castOther.timestamp) && Objects.equals(available, castOther.available);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, available);
    }

    public static final class Builder {

        private Instant timestamp;
        private Boolean status;

        private Builder() {
        }

        public Builder withTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withStatus(Boolean status) {
            this.status = status;
            return this;
        }

        public Availability build() {
            return new Availability(this);
        }
    }

}
