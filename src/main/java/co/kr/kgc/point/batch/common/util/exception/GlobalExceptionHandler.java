package co.kr.kgc.point.batch.common.util.exception;

import co.kr.kgc.point.batch.common.domain.BatchResponseDto;
import co.kr.kgc.point.batch.common.domain.ScheduleResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class  GlobalExceptionHandler {
    private static final Logger log = LogManager.getLogger();

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = BatchRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    private BatchResponseDto handleBatchRequestException(BatchRequestException e) {
        BatchResponseDto batchResponseDto = new BatchResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{e.getE().getMessage()}, null))
                .build();

        return batchResponseDto;
    }

    @ExceptionHandler(value = ScheduleRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    private ScheduleResponseDto handleScheduleRequestException(ScheduleRequestException e) {
        ScheduleResponseDto scheduleResponseDto
                = new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getE().getMessage()}, null))
                .build();

        return scheduleResponseDto;
    }
}