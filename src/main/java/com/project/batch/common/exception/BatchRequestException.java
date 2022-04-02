/*
 * @file : com.project.batch.common.exception.BatchRequestException.java
 * @desc : BatchController에서 호출하는 서비스에서 발생하는 Exception 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.common.exception;

public class BatchRequestException extends RuntimeException {
    /* 코드 */
    private String code;
    /* args */
    private String args;
    /* Exception */
    private Exception e;


    public BatchRequestException(String code, String param) {
        this.code = code;
        this.args = param;
    }

    public BatchRequestException(String code, String param, Exception e) {
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

    public void setArgs(String Args) {
        this.args = args;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
