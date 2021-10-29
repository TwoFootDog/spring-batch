package co.kr.kgc.point.batch.common.util.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
