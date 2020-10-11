package statefull.geofencing.faas.realtime.fencing.event;

//todo I guess useless (alarming can be done here with another KTable)
public class MoverAndFenceIntersectionUpdate {
    String moverId;
    String fenceWkt;
    boolean intersects;
}
