/*
 * @file : com.project.batch.domain.common.dto.BatchResponseDto.java
 * @desc : Spring Batch 서비스의 응답 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.common.dto;

import com.project.batch.domain.common.util.MessageUtil;

public class BatchResponseDto {
    /* Job 실행 ID */
    private long jobExecutionId;
    /* 배치 Job 이름 */
    private String jobName;
    /* 요청일시 */
    private String requestDate;
    /* 응답코드 */
    private String resultCode;
    /* 응답메시지 */
    private String resultMessage;

    public static class Builder {
        private long jobExecutionId;
        private String jobName;
        private String requestDate;
        private String resultCode;
        private String resultMessage;

        public Builder() {}

        public Builder jobExecutionId(long jobExecutionId) {
            this.jobExecutionId = jobExecutionId;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder requestDate(String requestDate) {
            this.requestDate = requestDate;
            return this;
        }

        public Builder resultCodeMsg(String messageId, Object... messageArgs) {
            this.resultCode = MessageUtil.getCode(messageId);
            this.resultMessage = MessageUtil.getMessage(messageId, messageArgs);
            return this;
        }

        public BatchResponseDto build() {
            BatchResponseDto batchResponseDto = new BatchResponseDto();
            batchResponseDto.jobExecutionId = jobExecutionId;
            batchResponseDto.jobName = jobName;
            batchResponseDto.requestDate = requestDate;
            batchResponseDto.resultCode = resultCode;
            batchResponseDto.resultMessage = resultMessage;
            return batchResponseDto;
        }
    }

    public long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
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
