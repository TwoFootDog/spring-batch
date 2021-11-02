package kr.co.kgc.point.batch.domain.common.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class ScheduleResponseDto {
    private String jobName;
    private String jobGroup;
    private Date startTime;
    private String cronExpression;
    private String resultCode;
    private String resultMessage;

    public static class Builder {
        private String jobName;
        private String jobGroup;
        private Date startTime;
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

        public Builder startTime(Date startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder cronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }

        public Builder resultCode(String resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public Builder resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
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