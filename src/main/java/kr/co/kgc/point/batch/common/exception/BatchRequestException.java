/*
 * @file : kr.co.kgc.point.batch.common.exception.BatchRequestException.java
 * @desc : RestTemplate에 대한 설정파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

//@Data
//@EqualsAndHashCode(callSuper = false)
public class BatchRequestException extends RuntimeException {

    private Exception e;
    private String message;


    public BatchRequestException(Exception e) {
        this.e = e;
    }
    public BatchRequestException(String message) {
        this.message = message;
    }

    public BatchRequestException(Exception e, String message) {
        this.e = e;
        this.message = message;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
