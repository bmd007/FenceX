package statefull.geofencing.faas.realtime.fencing.exception;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class ServiceUnavailableException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ServiceUnavailableException(String message) {
        super(SERVICE_UNAVAILABLE, message);
    }
}