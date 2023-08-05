package corpoints.server.Server.util.Advice;

import corpoints.server.Server.util.PersonErrorResponse;
import corpoints.server.Server.util.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(testException.class)
    public ResponseEntity<PersonErrorResponse> handleException(testException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                /*e.getMessage()*/"Test error",
                System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.OK);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<PersonErrorResponse> handleException(GroupNotFoundException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Группа не найдена",
                System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.OK);
    }

    @ExceptionHandler(YouHaveGroupException.class)
    public ResponseEntity<PersonErrorResponse> handleException(YouHaveGroupException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Вы уже состоите в группе",
                System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.OK);
    }
    @ExceptionHandler(NotEnoughPointsException.class)
    public ResponseEntity<PersonErrorResponse> handleException(NotEnoughPointsException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Недостаточно очков",
                System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.OK);
    }

    @ExceptionHandler(UncorrectException.class)
    public ResponseEntity<PersonErrorResponse> handleException(UncorrectException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Неправильный запрос.",
                System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.OK);
    }
}