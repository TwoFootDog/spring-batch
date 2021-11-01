package kr.co.kgc.point.batch.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
