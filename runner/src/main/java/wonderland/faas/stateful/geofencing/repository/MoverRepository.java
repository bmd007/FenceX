package wonderland.faas.stateful.geofencing.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wonderland.faas.stateful.geofencing.domain.Availability;
import wonderland.faas.stateful.geofencing.domain.Coordinate;
import wonderland.faas.stateful.geofencing.domain.Mover;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

@Repository
public class MoverRepository {

    private static final int PRECISION = 8;
    private static final String POINT_FORMAT = String.format("%%.%df %%.%df", PRECISION, PRECISION);
    private static final Pattern POINT_REGEX = Pattern.compile("POINT\\s*\\(([0-9\\.]+) ([0-9\\.]+)\\)");
    private JdbcTemplate jdbc;

    public MoverRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Save (insert or update) a {@link Mover}.
     */
    public void save(Mover mover) {
        jdbc.execute(String.format(
                "merge into movers (id, position, availability_status, availability_timestamp)"
                        + " key (id)"
                        + " values ('%s', 'POINT(%s)', '%s', '%s');",
                mover.getId(),
                format(mover.getPosition()),
                mover.getAvailability().getAvailable(),
                mover.getAvailability().getTimestamp()));
    }

    /**
     * @param key to identify the mover.
     * @return a {@link Mover} or null.
     */
    @Nullable
    public Mover get(String key) {
        try {
            var sql = String.format("select * from movers where id = '%s'", key);
            return jdbc.queryForObject(sql, (RowMapper<Mover>) this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * @return all movers.
     */
    public List<Mover> getAll() {
        return jdbc.query("select * from movers;", (RowMapper<Mover>) this::mapRow);
    }

    /**
     * Execute a range query by key (sorted by the string components).
     *
     * @return a list of movers where the keys are in range.
     */
    public List<Mover> getInRange(String from, String to) {
        return jdbc.query("select * from movers where (id between ? and ?);", (RowMapper<Mover>) this::mapRow, from, to);
    }

    /**
     * Delete mover by key.
     */
    public void delete(String key) {
        jdbc.execute(String.format("delete from movers where id = '%s';", key));
    }

    /**
     * Delete all movers.
     */
    public void deleteAll() {
        jdbc.execute("delete from movers;");
    }

    /**
     * @return how many movers have saved.
     */
    public long count() {
        return jdbc.queryForObject("select count(*) from movres;", Long.class);
    }

    /**
     * Query by a polygon.
     *
     * @param polygon a list of at least 3 points.
     * @return a list of movers with coordinates inside the polygon.
     */
    public List<Mover> query(List<Coordinate> polygon) {
        checkArgument(polygon.size() > 2, "Incomplete polygon");
        var polygonString = Stream.concat(polygon.stream(), Stream.of(polygon.get(0))) // ends with the first.
                .map(this::format)
                .collect(Collectors.joining(","));
        var sql = String.format("select * from movers where position && 'POLYGON((%s))';", polygonString);
        return jdbc.query(sql, (RowMapper<Mover>) this::mapRow);
    }

    private Mover mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Mover.builder()
                .withId(rs.getString("id"))
                .withPosition(parsePoint(rs.getString("position")))
                .withAvailability(Availability.builder()
                        .withStatus(rs.getBoolean("availability_status"))
                        .withTimestamp(rs.getTimestamp("availability_timestamp").toInstant())
                        .build())
                .build();
    }

    private String format(Coordinate point) {
        return String.format(POINT_FORMAT, point.getLongitude(), point.getLatitude());
    }

    private Coordinate parsePoint(String point) {
        var matcher = POINT_REGEX.matcher(point);
        if (matcher.matches()) {
            var lon = Double.parseDouble(matcher.group(1)); // lon => x
            var lat = Double.parseDouble(matcher.group(2)); // lat => y
            return new Coordinate(lat, lon);
        }
        throw new IllegalStateException("Cannot parse point string: " + point);
    }
}
