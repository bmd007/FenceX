package statefull.geofencing.faas.realtime.fencing.event;

//todo this goes into/comes from event-log internal topic (event sourcing)
public class FenceForMoverUpdatedEvent {
    String moverId;
    String fenceWkt;
}
