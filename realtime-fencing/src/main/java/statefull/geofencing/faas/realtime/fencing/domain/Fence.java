package statefull.geofencing.faas.realtime.fencing.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@JsonDeserialize(builder = Fence.Builder.class)
public class Fence {

    private String kwt;

    public Fence() {
    }

    private Fence(Builder builder) {
        this.kwt = builder.kwt;
    }

    public String getKwt() {
        return kwt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("kwt", kwt)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String kwt;

        public Builder withKwt(String kwt) {
            this.kwt = kwt;
            return this;
        }

        public Fence build() {
            return new Fence(this);
        }
    }
}