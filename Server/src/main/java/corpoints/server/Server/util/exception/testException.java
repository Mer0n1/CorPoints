package corpoints.server.Server.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*@ResponseStatus(code = HttpStatus.BAD_REQUEST,
        reason = "the user is already in blacklist with the specified user")*/
public class testException extends RuntimeException{
}
