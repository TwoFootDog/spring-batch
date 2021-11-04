/*
 * @file : kr.co.kgc.point.batch.common.exception.ScheduleRequestException.java
 * @desc : ScheduleController에서 호출하는 서비스에서 발생하는 Exception 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.common.exception;

public class ScheduleRequestException extends RuntimeException {

    /* Exception */
    private Exception e;
    /* 메시지 */
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
