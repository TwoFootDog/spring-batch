package kr.co.kgc.point.batch.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

//@Data
//@EqualsAndHashCode(callSuper = false)
public class ScheduleRequestException extends RuntimeException {

    private Exception e;
    private String message;


    public ScheduleRequestException(Exception e) {
        this.e = e;
    }
    public ScheduleRequestException(String message) {
        this.message = message;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
