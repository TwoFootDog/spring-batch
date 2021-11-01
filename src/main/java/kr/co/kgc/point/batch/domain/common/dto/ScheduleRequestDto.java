package kr.co.kgc.point.batch.domain.common.dto;

public class ScheduleRequestDto {
    private String jobName;
    private String jobGroup;
    private String startTime;
    private String cronExpression;
    private String desc;

    public ScheduleRequestDto(String jobName, String jobGroup, String startTime, String cronExpression, String desc) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.startTime = startTime;
        this.cronExpression = cronExpression;
        this.desc = desc;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
