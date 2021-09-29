package co.kr.kgc.point.batch.domain;

import lombok.Data;

@Data
public class SchedulerRequestDto {
    private String jobName;
    private String jobGroup;
    private String cronExpression;
    private Long repeatTime;
    private String desc;
}
