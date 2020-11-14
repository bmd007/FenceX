package statefull.geofencing.faas.bench.marking.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@JsonDeserialize(builder = FenceDto.Builder.class)
public class FenceDto {

    private final String wkt;
    private final String moverId;

    private FenceDto(Builder builder) {
        wkt = builder.wkt;
        moverId = builder.moverId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(FenceDto copy) {
        Builder builder = new Builder();
        builder.wkt = copy.getWkt();
        builder.moverId = copy.getMoverId();
        return builder;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return moverId == null || moverId.isEmpty() || moverId.isBlank() || wkt == null || wkt.isEmpty() || wkt.isBlank();
    }

    public Builder cloneBuilder() {
        return newBuilder(this);
    }

    public String getWkt() {
        return wkt;
    }

    public String getMoverId() {
        return moverId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("wkt", wkt)
                .add("moverId", moverId)
                .toString();
    }

    public static final class Builder {
        private String wkt;
        private String moverId;

        private Builder() {
        }

        public Builder withWkt(String val) {
            wkt = val;
            return this;
        }

        public Builder withMoverId(String val) {
            moverId = val;
            return this;
        }

        public FenceDto build() {
            return new FenceDto(this);
        }
    }
}