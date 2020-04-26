package wonderland.faas.stateful.geofencing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wonderland.faas.stateful.geofencing.domain.Availability;
import wonderland.faas.stateful.geofencing.domain.Coordinate;
import wonderland.faas.stateful.geofencing.domain.Mover;
import wonderland.faas.stateful.geofencing.dto.CoordinateDto;
import wonderland.faas.stateful.geofencing.dto.PolygonDto;
import wonderland.faas.stateful.geofencing.repository.MoverRepository;

import java.time.Instant;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wonderland.faas.stateful.geofencing.config.Stores.MOVER_IN_MEMORY_STATE_STORE;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "mover-position-updates",
        MOVER_IN_MEMORY_STATE_STORE + "-" + "stateful-geofencing-faas-changelog",
        "event_log"
})
class MoverResourceTest {

    private static final PolygonDto POLYGON = PolygonDto.builder()
            .addPoint(new CoordinateDto(5, 5))
            .addPoint(new CoordinateDto(15, 5))
            .addPoint(new CoordinateDto(15, 15))
            .addPoint(new CoordinateDto(5, 15))
            .build();
    private static final String KEY = "ABC123";

    MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MoverRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // save one car in (11,12)
        repository.save(Mover.builder()
                .withId(KEY)
                .withPosition(new Coordinate(11, 12))
                .withAvailability(Availability.builder()
                        .withTimestamp(Instant.now().minusSeconds(15))
                        .withStatus(true)
                        .build())
                .build());
    }

    @Test
    void testBox() throws Exception {
        mvc.perform(get("/api/movers/box")
                .param("boxStartLatitude", "5.0")
                .param("boxStartLongitude", "5.0")
                .param("boxEndLatitude", "15.0")
                .param("boxEndLongitude", "15.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movers[0].id", is(KEY)))
                .andExpect(jsonPath("$.movers[0].position.latitude", is(11.0)))
                .andExpect(jsonPath("$.movers[0].position.longitude", is(12.0)));
    }

    @Test
    void testPolygon() throws Exception {
        mvc.perform(post("/api/movers/polygon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(POLYGON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movers[0].regNo", is(KEY)))
                .andExpect(jsonPath("$.movers[0].position.latitude", is(11.0)))
                .andExpect(jsonPath("$.movers[0].position.longitude", is(12.0)));
    }

    @Test
    void testPolygonNoResults() throws Exception {
        mvc.perform(post("/api/movers/polygon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(PolygonDto.builder()
                        .addPoint(new CoordinateDto(-20, -20))
                        .addPoint(new CoordinateDto(10, -20))
                        .addPoint(new CoordinateDto(10, 10))
                        .addPoint(new CoordinateDto(-20, 10))
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movers", is(empty())));
    }

    @Test
    void testPolygonInvalidTooFewPoints() throws Exception {
        mvc.perform(post("/api/movers/polygon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(PolygonDto.builder()
                        .addPoint(new CoordinateDto(15, 15))
                        .addPoint(new CoordinateDto(5, 15))
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPolygonInvalidOutOfBounds() throws Exception {
        mvc.perform(post("/api/movers/polygon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(PolygonDto.builder()
                        .addPoint(new CoordinateDto(-100, 0))
                        .addPoint(new CoordinateDto(15, 0))
                        .addPoint(new CoordinateDto(15, 200))
                        .addPoint(new CoordinateDto(-100, 200))
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPolygonFilterByAvailability() throws Exception {
        mvc.perform(post("/api/movers/polygon")
                .param("availability", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(POLYGON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movers", is(empty())));
    }

    @Test
    void testPolygonFilterByAvailabilityMultiple() throws Exception {
        mvc.perform(post("/api/movers/polygon")
                .param("availability", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(POLYGON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movers[0].id", is(KEY)));
    }

    @Test
    void testPolygonFilterByMaxAge() throws Exception {
        mvc.perform(post("/api/movers/polygon")
                .param("maxAge", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(POLYGON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movers", is(empty())));
    }
}
