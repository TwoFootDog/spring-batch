package co.kr.kgc.point.batch.common.util.exception;

import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class ExceptionMessageHandler {

    private String resultCode;
    private String resultMessage;

    @Autowired
    private MessageSource messageSource;

    public String getMessage(Exception e) {
        if (e instanceof NoSuchJobException) {
            this.resultCode = messageSource.getMessage("batch.response.fail.code", new String[]{}, null);
            this.resultMessage = messageSource.getMessage("batch.response.fail.message", new String[]{}, null);
        }
    }
}
