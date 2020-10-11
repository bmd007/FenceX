package statefull.geofencing.faas.location.update.processor.config;

public class Topics {
    //todo move creation of this topic to Location-Update-Publisher service
    // source topic for mover position updates
    public static final String MOVER_POSITION_UPDATES_TOPIC = "mover-position-updates";

    //Owned by this service
    public static final String MOVER_UPDATES_TOPIC = "mover-updates";
//    public static final String EVENT_LOG = "event_log";
}
