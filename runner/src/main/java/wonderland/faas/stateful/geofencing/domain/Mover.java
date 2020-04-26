package wonderland.faas.stateful.geofencing.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import java.time.Instant;
import java.util.Objects;

@JsonDeserialize(builder = Mover.Builder.class)
public class Mover {

    private String id;
    private Coordinate position;
    private Availability availability;

    private Mover(Builder builder) {
        id = builder.id;
        position = builder.position;
        availability = builder.availability;
    }

    public static Mover define(String id) {
        return Mover.builder()
                .withId(id)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Mover copy) {
        Builder builder = new Builder();
        builder.id = copy.getId();
        builder.position = copy.getPosition();
        builder.availability = copy.getAvailability();
        return builder;
    }

    public String getId() {
        return id;
    }

    public Coordinate getPosition() {
        return position;
    }

    public Availability getAvailability() {
        return availability;
    }

    public boolean isUndefined() {
        return id.isBlank();
    }

    public Mover updatedAvailability(boolean status, Coordinate position) {
        return cloneBuilder()
                .withAvailability(Availability.builder()
                        .withTimestamp(Instant.now())
                        .withStatus(status)
                        .build())
                .withPosition(position)
                .build();
    }

    /**
     * @return if the mover has received any update in the given seconds.
     */
    public boolean receivedUpdate(Long maxAgeInSeconds) {
        var limit = Instant.now().minusSeconds(maxAgeInSeconds);
        return availability.getTimestamp().isAfter(limit);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("position", position)
                .add("availabilityStatus", availability)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mover mover = (Mover) o;
        return Objects.equals(id, mover.id) &&
                Objects.equals(position, mover.position) &&
                Objects.equals(availability, mover.availability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, position, availability);
    }

    public Builder cloneBuilder() {
        return builder(this);
    }

    public static final class Builder {
        private String id;
        private Coordinate position = Coordinate.nullValue();
        private Availability availability = Availability.nullValue();

        private Builder() {
        }

        public Builder withId(String val) {
            id = val;
            return this;
        }

        public Builder withPosition(Coordinate val) {
            position = val;
            return this;
        }

        public Builder withAvailability(Availability val) {
            availability = val;
            return this;
        }

        public Mover build() {
            return new Mover(this);
        }
    }
}
