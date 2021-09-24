package co.kr.kgc.point.batch.job.Quartz;

import lombok.Data;

@Data
public class SchedulerJobInfo {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String jobClass;
    private String cronExpression;
    private String desc;
    private String interfaceName;
    private Long repeatTime;
    private Boolean cronJob;    // true : CronJob, false : SimpleJob

}
