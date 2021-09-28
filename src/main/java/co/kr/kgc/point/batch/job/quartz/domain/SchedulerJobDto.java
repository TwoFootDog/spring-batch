package co.kr.kgc.point.batch.job.quartz.domain;

import lombok.Data;

@Data
public class SchedulerJobDto {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private String jobClass;
    private String cronExpression;
    private Long repeatTime;
    private String desc;
    private boolean cronJob;    // true : CronJob, false : SimpleJob
}
