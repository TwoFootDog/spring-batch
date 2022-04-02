/*
 * @file : com.project.batch.domain.common.dto.ScheduleRequestDto.java
 * @desc : Quartz Schedule 서비스 요청 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.common.dto;

public class ScheduleRequestDto {
    /* Job Schedule 이름 */
    private String jobName;
    /* Job Schedule 그룹 */
    private String jobGroup;
    /* Job Schedule 시작시간 */
    private String startTime;
    /* Job Schedule 스케쥴링 표현식 */
    private String cronExpression;
    /* Job Schedule 상세설명 */
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
