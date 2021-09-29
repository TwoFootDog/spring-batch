package co.kr.kgc.point.batch.domain;

import lombok.Data;

@Data
public class SchedulerRequestDto {
    private String jobId;
    private String jobName;
    private String jobGroup;
    private String jobClass;
    private String cronExpression;
    private Long repeatTime;
    private String desc;
    private boolean cronJob;    // true : CronJob, false : SimpleJob
}
