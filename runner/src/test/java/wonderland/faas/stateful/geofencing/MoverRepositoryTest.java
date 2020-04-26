package wonderland.faas.stateful.geofencing;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wonderland.faas.stateful.geofencing.domain.Availability;
import wonderland.faas.stateful.geofencing.domain.Coordinate;
import wonderland.faas.stateful.geofencing.domain.Mover;
import wonderland.faas.stateful.geofencing.repository.MoverRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext
class MoverRepositoryTest {

    private static final String MOVER_KEY = "AMD222";
    private static final Mover MOVER = Mover.builder()
            .withId(MOVER_KEY)
            .withAvailability(Availability.builder()
                    .withTimestamp(Instant.now())
                    .withStatus(false)
                    .build())
            .withPosition(new Coordinate(2.4, 8.16))
            .build();


    @Autowired
    JdbcTemplate jdbc;

    MoverRepository repository;

    private static MoverMatcher matches() {
        return new MoverMatcher(MOVER);
    }

    @BeforeEach
    void setUp() throws Exception {
        repository = new MoverRepository(jdbc);
    }

    @Test
    void testSaveAndGetByKey() {
        repository.save(MOVER);
        assertThat(repository.get(MOVER_KEY), matches());
    }

    @Test
    void testGetAllEmpty() {
        assertTrue(repository.getAll().isEmpty());
    }

    @Test
    void testGetAll() {
        repository.save(MOVER);
        assertThat(repository.getAll(), contains(matches()));
    }

    @Test
    void testGetRangeEmpty() {
        var from = "AAA000";
        var to = "ZZZ999";
        assertThat(repository.getInRange(from, to), is(empty()));
    }

    @Test
    void testGetRangeInRange() {
        var from = "AAA000";
        var to = "ZZZ999";
        repository.save(MOVER);
        assertThat(repository.getInRange(from, to), contains(matches()));
    }

    @Test
    void testGetRangeOutsideRange() {
        var from = "AAA000";
        var to = "ZZZ999";
        repository.save(MOVER);
        assertThat(repository.getInRange(from, to), is(empty()));
    }

    @Test
    void testDelete() {
        repository.save(MOVER);
        repository.delete(MOVER_KEY);
        assertThat(repository.getAll(), is(empty()));
    }

    @Test
    void testDeleteAll() {
        repository.save(MOVER);
        repository.deleteAll();
        assertThat(repository.getAll(), is(empty()));
    }

    @Test
    void testQuery() {
        repository.save(MOVER);
        assertThat(repository.query(List.of(
                new Coordinate(1, 7),
                new Coordinate(4, 7),
                new Coordinate(4, 9),
                new Coordinate(1, 9))), contains(matches()));
    }

    @Test
    void testQueryOutside() {
        repository.save(MOVER);
        assertThat(repository.query(List.of(
                new Coordinate(3, 7),
                new Coordinate(4, 7),
                new Coordinate(4, 9),
                new Coordinate(3, 9))), is(empty()));
    }

    //todo what is happening here??
    private static class MoverMatcher extends TypeSafeMatcher<Mover> {

        private final Mover expected;

        private MoverMatcher(Mover expected) {
            this.expected = expected;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("Expected: %s", expected));
        }

        @Override
        protected boolean matchesSafely(Mover item) {
            return item != null &&
                    expected.getId().equals(item.getId()) &&
                    expected.getPosition().equals(item.getPosition()) &&
                    matchesStatus(expected.getAvailability(), item.getAvailability());
        }

        private boolean matchesStatus(Availability st1, Availability st2) {
            if (st1 == null) {
                return st2 == null || st2.getAvailable() == null;
            }
            if (st2 == null) {
                return st1.getAvailable() == null;
            }
            return st1.getTimestamp()
                    .truncatedTo(ChronoUnit.SECONDS)
                    .equals(st2.getTimestamp().truncatedTo(ChronoUnit.SECONDS)) &&
                    st1.getAvailable().equals(st2.getAvailable());
        }
    }
}
