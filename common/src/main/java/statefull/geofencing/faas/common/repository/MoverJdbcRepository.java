package statefull.geofencing.faas.common.repository;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import statefull.geofencing.faas.common.domain.Coordinate;
import statefull.geofencing.faas.common.domain.Mover;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

@Repository
public class MoverJdbcRepository {

    public final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326);
    private final JdbcTemplate jdbc;

    //todo incorporate time stamp into queries for checking availability
    private final WKTReader wktReader = new WKTReader(new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), 4326));

    public MoverJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public JdbcTemplate getJdbc() {
        return jdbc;
    }

    public WKTReader getWktReader() {
        return wktReader;
    }

    /**
     * Save (insert or update) a {@link Mover}.
     */
    public void save(Mover mover) {
        jdbc.execute(String.format(
                "merge into movers (id, last_location, updated_at)"
                        + " key (id)"
                        + " values ('%s', '%s', '%s');",
                mover.getId(),
                coordinateToWktPoint(mover.getLastLocation()),
                mover.getUpdatedAt()
        ));
    }

    /**
     * @param key to identify the mover.
     * @return a {@link Mover} or null.
     */
    @Nullable
    public Mover get(String key) {
        try {
            var sql = String.format("select * from movers where id = '%s'", key);
            return jdbc.queryForObject(sql, this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * @return all movers.
     */
    public List<Mover> getAll() {
        return jdbc.query("select * from movers;", this::mapRow);
    }

    /**
     * Execute a range query by key (sorted by the string components).
     *
     * @return a list of movers where the keys are in range.
     */
    public List<Mover> getInRange(String from, String to) {
        return jdbc.query("select * from movers where (id between ? and ?);", this::mapRow, from, to);
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

    //todo add support for maxAge

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
     * TODO query might be wrong. Polygon to text using points is different from interpolation
     */
    public List<Mover> query(List<Point> polygon) {
        checkArgument(polygon.size() > 2, "Incomplete polygon");
        var polygonString = Stream.concat(polygon.stream(), Stream.of(polygon.get(0))) // ends with the first.
                .map(point -> point.toText())
                .collect(Collectors.joining(","));
        var sql = String.format("select * from movers where last_location && 'POLYGON((%s))';", polygonString);
        return jdbc.query(sql, this::mapRow);
    }

    /**
     * Query by a polygon.
     *
     * @param polygon a list of at least 3 points.
     * @return a list of movers with coordinates inside the polygon.
     */
    //todo add support for maxAge
    public List<Mover> query(Polygon polygon) {
        var sql = String.format("select * from movers where last_location && %s';", polygon.toText());
        return jdbc.query(sql, this::mapRow);
    }

    private Mover mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Mover.newBuilder()
                .withId(rs.getString("id"))
                .withLastLocation(wktPointToCoordinate(rs.getString("last_location")))
                .withUpdatedAt(rs.getTimestamp("updatedAt").toInstant())
                .build();
    }

    //todo handle ull fields
    private Coordinate wktPointToCoordinate(String wktPoint) {
        try {
            var point = (Point) this.wktReader.read(wktPoint);
            return Coordinate.of(point.getX(), point.getY());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Cannot parse point string: " + wktPoint);
    }

    //todo handle ull fields
    private String coordinateToWktPoint(Coordinate coordinate) {
        org.locationtech.jts.geom.CoordinateXY jtsCoordinate = new CoordinateXY(coordinate.getLatitude(), coordinate.getLongitude());
        return GEOMETRY_FACTORY.createPoint(jtsCoordinate).toText();
    }
}
