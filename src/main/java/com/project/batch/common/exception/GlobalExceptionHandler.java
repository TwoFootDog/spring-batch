/*
 * @file : com.project.batch.common.exception.GlobalExceptionHandler.java
 * @desc : Controller를 통해 요청된 서비스(배치 job 시작/종료, 스케쥴 등록/삭제/변경/시작/중지 등)에서 예외가 발생했을 때
 *         예외를 처리해주는 Exception Handler
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.common.exception;

import com.project.batch.domain.common.dto.ErrorResponseDto;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class  GlobalExceptionHandler {

    private final MessageSource messageSource;
    private static final String SYSTEM_ERROR_CODE = "A9999";

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /*
     * @method : handleBatchRequestException
     * @desc : BatchController를 통한 배치 Job 실행/종료 서비스 수행 시 BatchRequestException 이 발생할 경우 호출
     * @param :
     * @return :
     * */
    @ExceptionHandler(value = BatchRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    private ErrorResponseDto handleBatchRequestException(BatchRequestException e) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setResultCodeMsg(e.getCode(), e.getArgs());
        return errorResponseDto;
    }

    /*
     * @method : handleScheduleRequestException
     * @desc : ScheduleController를 통한 배치 Job 실행/종료 서비스 수행 시 ScheduleRequestException 이 발생할 경우 호출
     * @param :
     * @return :
     * */
    @ExceptionHandler(value = ScheduleRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    private ErrorResponseDto handleScheduleRequestException(ScheduleRequestException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setResultCodeMsg(e.getCode(), e.getArgs());
        return errorResponseDto;
    }

    /*
     * @method : handleException
     * @desc : Exception 이 발생할 경우 호출
     * @param :
     * @return :
     * */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    private ErrorResponseDto handleException(Exception e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setResultCodeMsg(SYSTEM_ERROR_CODE, messageSource.getMessage(SYSTEM_ERROR_CODE, new String[]{e.getMessage()}, null));
        return errorResponseDto;
    }
}