package statefull.geofencing.faas.realtime.fencing.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import org.locationtech.jts.geom.Geometry;

@JsonDeserialize(builder = Fence.Builder.class)
public class Fence {

    private String wkt;
    private String moverId;

    private Fence(Builder builder) {
        wkt = builder.wkt;
        moverId = builder.moverId;
    }


    public static Fence define(String wkt, String moverId){
        return newBuilder().withMoverId("").withWkt("").build();
    }


    public static Fence defineEmpty(){
        return newBuilder().withMoverId("").withWkt("").build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder cloneBuilder(){
        return newBuilder(this);
    }

    public static Builder newBuilder(Fence copy) {
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

        public Fence build() {
            return new Fence(this);
        }
    }
}