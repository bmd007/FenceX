package statefull.geofencing.faas.realtime.fencing.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@JsonDeserialize(builder = FenceIntersectionStatus.Builder.class)
//todo add more fields like timeStamp, fenceWkt
public class FenceIntersectionStatus {

    private Boolean isInFence;
    private String moverId;

    private FenceIntersectionStatus(Builder builder) {
        isInFence = builder.isInFence;
        moverId = builder.moverId;
    }

    public static FenceIntersectionStatus define(Boolean isInFence, String moverId){
        return newBuilder().withMoverId(moverId).withIsInFence(isInFence).build();
    }

    public static FenceIntersectionStatus defineEmpty(){
        return newBuilder().withMoverId("").withIsInFence(Boolean.FALSE).build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder cloneBuilder(){
        return newBuilder(this);
    }

    public static Builder newBuilder(FenceIntersectionStatus copy) {
        Builder builder = new Builder();
        builder.isInFence = copy.getIsInFence();
        builder.moverId = copy.getMoverId();
        return builder;
    }

    public Boolean getIsInFence() {
        return isInFence;
    }

    public String getMoverId() {
        return moverId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isInFence", isInFence)
                .add("moverId", moverId)
                .toString();
    }

    public static final class Builder {
        private Boolean isInFence;
        private String moverId;

        private Builder() {
        }

        public Builder withIsInFence(Boolean val) {
            isInFence = val;
            return this;
        }

        public Builder withMoverId(String val) {
            moverId = val;
            return this;
        }

        public FenceIntersectionStatus build() {
            return new FenceIntersectionStatus(this);
        }
    }
}