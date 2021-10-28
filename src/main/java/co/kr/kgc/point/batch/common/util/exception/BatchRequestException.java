package co.kr.kgc.point.batch.common.util.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
public class BatchRequestException extends RuntimeException {
    private Exception e;
    private String message;

    public BatchRequestException(Exception e) {
        this.e = e;
    }
    public BatchRequestException(String message) {
        this.message = message;
    }

}
