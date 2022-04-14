package com.project.batch.domain.common.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageUtil implements InitializingBean {
    @Resource
    private MessageSource source;
    static MessageSource messageSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        messageSource = source;
    }

//    public static String getCode(String messageId) {
//        return messageSource.getMessage(messageId, null, null);
//    }

    public static String getMessage(String messageId) {
        return messageSource.getMessage(messageId, null, null);
    }

    public static String getMessage(String messageId, Object... messageArgs) {
        return messageSource.getMessage(messageId, messageArgs, null);
    }
}
