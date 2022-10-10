package trying.cosmos.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Getter
public class CustomExceptionEntity {

    private final String id;
    private final String code;
    private final String message;

    public CustomExceptionEntity(ExceptionType eType, Exception e) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.code = eType.getCode();
        this.message = eType.getMessage();
        log.info("[{}] Exception {}, {}", id, code, message, e);
    }
}
