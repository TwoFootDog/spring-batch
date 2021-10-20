package co.kr.kgc.point.batch.domain;

public class ScheduleRequestDto {
    private String jobName;
    private String jobGroup;
    private String cronExpression;
    private Long repeatTime;
    private String desc;

    public ScheduleRequestDto(String jobName, String jobGroup, String cronExpression, Long repeatTime, String desc) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.cronExpression = cronExpression;
        this.repeatTime = repeatTime;
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

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Long getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(Long repeatTime) {
        this.repeatTime = repeatTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
