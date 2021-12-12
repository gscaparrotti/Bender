package com.github.gscaparrotti.bender.springControllers;

import com.github.gscaparrotti.bender.services.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerUtils {

    static <T> ResponseEntity<T> resultToResponseEntity(final Result<T> result) {
        final Result.ResultType resultType = result.getResultType();
        switch (resultType) {
            case OK:
                return new ResponseEntity<>(result.getValue(), HttpStatus.OK);
            case CREATED:
                return new ResponseEntity<>(result.getValue(), HttpStatus.CREATED);
            case NO_CONTENT:
                return new ResponseEntity<>(result.getValue(), HttpStatus.NO_CONTENT);
            case CONFLICT:
                return new ResponseEntity<>(result.getValue(), HttpStatus.CONFLICT);
            case BAD_REQUEST:
                return new ResponseEntity<>(result.getValue(), HttpStatus.BAD_REQUEST);
            case ERROR:
                return new ResponseEntity<>(result.getValue(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
