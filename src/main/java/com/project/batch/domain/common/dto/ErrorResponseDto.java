/*
 * @file : com.project.batch.domain.common.dto.ErrorResponseDto.java
 * @desc : Spring Batch 서비스 및 Quartz Schedule 서비스 에러 응답 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.common.dto;

import com.project.batch.domain.common.util.MessageUtil;

public class ErrorResponseDto {
    /* 응답코드 */
    private String resultCode;
    /* 응답메시지 */
    private String resultMessage;

    public void setResultCodeMsg(String messageId, Object... messageArgs) {
        this.resultCode = messageId;
        this.resultMessage = MessageUtil.getMessage(messageId, messageArgs);
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
