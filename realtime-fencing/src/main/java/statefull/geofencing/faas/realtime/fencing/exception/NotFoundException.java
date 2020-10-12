package statefull.geofencing.faas.realtime.fencing.exception;

import statefull.geofencing.faas.realtime.fencing.dto.ErrorDto;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class NotFoundException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(NOT_FOUND, ErrorDto.builder().withCode(NOT_FOUND.toString()).withMessage(message).build());
    }
}

