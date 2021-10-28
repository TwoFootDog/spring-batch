package co.kr.kgc.point.batch.common.util.exception;

import co.kr.kgc.point.batch.domain.BatchResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class  GlobalExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class); // 로깅 객체

    @ExceptionHandler(value = BatchRequestException.class)   // ServiceException 발생 시 수행
    @ResponseStatus(HttpStatus.OK)
    private BatchResponseDto handleBatchRequestException(BatchRequestException e) {
        BatchResponseDto batchResponseDto = new BatchResponseDto();

//        batchResponseDto
        log.error(">> batch Fail. Error Code : {}, Error Log Message : {}"
                , e.getCode()
                , e.getLogMessage());

        if (e.getE()!=null) {
            log.error("@@@Service Fail Exception - UUID : {}, Exception message : {}, Exception StackTrace : {}"
                    , e.getE().getMessage()
                    , e.getE().getStackTrace());
        }
        return batchResponseDto;
    }
}