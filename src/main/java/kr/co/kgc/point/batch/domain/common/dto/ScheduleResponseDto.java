/*
 * @file : kr.co.kgc.point.batch.domain.common.dto.ScheduleResponseDto.java
 * @desc : Quartz Schedule 서비스 응답 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.common.dto;

import kr.co.kgc.point.batch.domain.common.util.MessageUtil;

import java.time.LocalDateTime;

public class ScheduleResponseDto {
    /* Job Schedule 이름 */
    private String jobName;
    /* Job Schedule 그룹 */
    private String jobGroup;
    /* Job Schedule 시작시간 */
    private LocalDateTime startTime;
    /* Job Schedule 스케쥴링 표현식 */
    private String cronExpression;
    /* 응답코드 */
    private String resultCode;
    /* 응답메시지 */
    private String resultMessage;

    public static class Builder {
        private String jobName;
        private String jobGroup;
        private LocalDateTime startTime;
        private String cronExpression;
        private String resultCode;
        private String resultMessage;

        public Builder() {
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder jobGroup(String jobGroup) {
            this.jobGroup = jobGroup;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder cronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }

        public Builder resultCodeMsg(String messageId, Object... messageArgs) {
            this.resultCode = MessageUtil.getCode(messageId);
            this.resultMessage = MessageUtil.getMessage(messageId, messageArgs);
            return this;
        }

        public ScheduleResponseDto build() {
            ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto();
            scheduleResponseDto.jobName = jobName;
            scheduleResponseDto.jobGroup = jobGroup;
            scheduleResponseDto.startTime = startTime;
            scheduleResponseDto.cronExpression = cronExpression;
            scheduleResponseDto.resultCode = resultCode;
            scheduleResponseDto.resultMessage = resultMessage;
            return scheduleResponseDto;
        }
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
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