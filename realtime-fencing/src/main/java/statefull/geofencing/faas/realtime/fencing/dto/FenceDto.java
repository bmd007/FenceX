package statefull.geofencing.faas.realtime.fencing.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@JsonDeserialize(builder = FenceDto.Builder.class)
public class FenceDto {

    private String wkt;

    public FenceDto() {
    }

    private FenceDto(Builder builder) {
        this.wkt = builder.wkt;
    }

    public String getWkt() {
        return wkt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("wkt", wkt)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String wkt;

        public Builder withWkt(String wkt) {
            this.wkt = wkt;
            return this;
        }

        public FenceDto build() {
            return new FenceDto(this);
        }
    }
}