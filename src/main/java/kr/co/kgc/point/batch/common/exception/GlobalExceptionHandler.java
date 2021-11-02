/*
 * @file : kr.co.kgc.point.batch.common.exception.GlobalExceptionHandler.java
 * @desc : Controller를 통해 요청된 서비스(배치 job 시작/종료, 스케쥴 등록/삭제/변경/시작/중지 등)에서 예외가 발생했을 때
 *         예외를 처리해주는 Exception Handler
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.common.exception;

import kr.co.kgc.point.batch.domain.common.dto.BatchResponseDto;
import kr.co.kgc.point.batch.domain.common.dto.ErrorResponseDto;
import kr.co.kgc.point.batch.domain.common.dto.ScheduleResponseDto;
import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class  GlobalExceptionHandler {

    private final MessageSource messageSource;


    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = BatchRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    private ErrorResponseDto handleBatchRequestException(BatchRequestException e) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto
                .Builder()
                .resultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{e.getMessage()}, null))
                .build();

        return errorResponseDto;
    }

    @ExceptionHandler(value = ScheduleRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    private ErrorResponseDto handleScheduleRequestException(ScheduleRequestException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto
                .Builder()
                .resultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                .build();

        return errorResponseDto;
    }
}