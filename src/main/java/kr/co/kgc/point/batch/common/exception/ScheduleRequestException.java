package kr.co.kgc.point.batch.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ScheduleRequestException extends RuntimeException {
    private Exception e;
    private String message;

    public ScheduleRequestException(Exception e) {
        this.e = e;
    }
    public ScheduleRequestException(String message) {
        this.message = message;
    }

}
