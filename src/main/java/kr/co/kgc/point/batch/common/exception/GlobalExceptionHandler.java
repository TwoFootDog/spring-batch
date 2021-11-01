package kr.co.kgc.point.batch.common.exception;

import kr.co.kgc.point.batch.domain.common.dto.BatchResponseDto;
import kr.co.kgc.point.batch.domain.common.dto.ScheduleResponseDto;
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
                .resultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{e.getE().getMessage()}, null))
                .build();

        return batchResponseDto;
    }

    @ExceptionHandler(value = ScheduleRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    private ScheduleResponseDto handleScheduleRequestException(ScheduleRequestException e) {
        ScheduleResponseDto scheduleResponseDto
                = new ScheduleResponseDto
                .Builder()
                .resultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getE().getMessage()}, null))
                .build();

        return scheduleResponseDto;
    }
}