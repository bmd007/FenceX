package statefull.geofencing.faas.realtime.fencing.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@JsonDeserialize(builder = FenceDto.Builder.class)
//todo add support for went into and left the fence (needs another KTable of MoverAndFenceIntersectionUpdate(events) --(state: boolean isInAFence))
public class FenceDto {

    private String wkt;
    private String moverId;

    private FenceDto(Builder builder) {
        wkt = builder.wkt;
        moverId = builder.moverId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder cloneBuilder(){
        return newBuilder(this);
    }

    public static Builder newBuilder(FenceDto copy) {
        Builder builder = new Builder();
        builder.wkt = copy.getWkt();
        builder.moverId = copy.getMoverId();
        return builder;
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