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
    /* 코드 */
    private String code;
    /* args */
    private String args;
    /* Exception */
    private Exception e;

    public ScheduleRequestException(String code, String args) {
        this.code = code;
        this.args = args;
    }
    public ScheduleRequestException(String code, String param, Exception e) {
        this.code = code;
        this.args = param;
        this.e = e;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
