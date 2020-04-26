package statefull.geofencing.faas.common.domain;

import com.google.common.base.MoreObjects;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.Objects;

public class Mover {

    private String id;

    private Point lastLocation;

    private Instant updatedAt;

    public Mover() {
    }

    public static Mover defineEmpty(){
        return newBuilder().withId("").build();
    }

    public Builder cloneBuilder(){
        return newBuilder(this);
    }

    private Mover(Builder builder) {
        id = builder.id;
        lastLocation = builder.lastLocation;
        updatedAt = builder.updatedAt;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Mover copy) {
        Builder builder = new Builder();
        builder.id = copy.getId();
        builder.lastLocation = copy.getLastLocation();
        builder.updatedAt = copy.getUpdatedAt();
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mover mover = (Mover) o;
        return Objects.equals(id, mover.id) &&
                Objects.equals(lastLocation, mover.lastLocation) &&
                Objects.equals(updatedAt, mover.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastLocation, updatedAt);
    }


    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("lastLocation", lastLocation)
                .add("updatedAt", updatedAt)
                .toString();
    }

    public String getId() {
        return id;
    }

    public Point getLastLocation() {
        return lastLocation;
    }

    public static final class Builder {
        private String id;
        private Point lastLocation;
        private Instant updatedAt;

        private Builder() {
        }

        public Builder withId(String val) {
            id = val;
            return this;
        }

        public Builder withLastLocation(Point val) {
            lastLocation = val;
            return this;
        }

        public Builder withUpdatedAt(Instant val) {
            updatedAt = val;
            return this;
        }

        public Mover build() {
            return new Mover(this);
        }
    }
}
