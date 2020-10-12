package statefull.geofencing.faas.realtime.fencing.exception;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class IllegalInputException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public IllegalInputException(String message) {
        super(BAD_REQUEST, message);
    }
}

