package statefull.geofencing.faas.realtime.fencing.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;


@JsonDeserialize(builder = FencesDto.Builder.class)
public class FencesDto {

    private List<FenceDto> fences;

    public FencesDto() {
    }

    private FencesDto(Builder builder) {
        this.fences = List.copyOf(builder.fences);
    }

    public static FencesDto singleFence(String wkt, String moverID) {
        return FencesDto.builder()
                .withFence(FenceDto.newBuilder()
                        .withMoverId(moverID)
                        .withWkt(wkt)
                        .build())
                .build();
    }

    public static FencesDto fences(List<FenceDto> fencesDtos) {
        return FencesDto.builder()
                .withFences(fencesDtos)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<FenceDto> getFences() {
        return fences;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fences", fences)
                .toString();
    }

    public static class Builder {

        private List<FenceDto> fences = new ArrayList<>();

        public Builder withFence(FenceDto fence) {
            fences.add(fence);
            return this;
        }

        public Builder withFences(List<FenceDto> fences) {
            this.fences = fences;
            return this;
        }

        public Boolean isEmpty() {
            return fences.isEmpty();
        }

        public FencesDto build() {
            return new FencesDto(this);
        }
    }
}