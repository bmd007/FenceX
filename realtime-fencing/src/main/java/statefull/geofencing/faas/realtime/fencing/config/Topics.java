package statefull.geofencing.faas.realtime.fencing.config;

public class Topics {
    public static final String MOVER_POSITION_UPDATES_TOPIC = "mover-position-updates";
    //owned by this service
    public static final String FENCE_EVENT_LOG = "fence_event_log"; //for now it will be used only for fence creation/deletion (value:String).
    // Polymorphism can be used later to provide more event types and a complicated processor as subscriber.
    //todo fence-intersection-events
}
