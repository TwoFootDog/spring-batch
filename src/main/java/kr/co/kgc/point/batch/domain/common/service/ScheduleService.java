/*
 * @file : kr.co.kgc.point.batch.domain.common.service.ScheduleService.java
 * @desc : ScheduleController에서 직접 호출해주는 Spring Batch 관련 서비스가 명시된 클래스
 *         (스케쥴러 등록/수정/삭제 및 스케쥴러 시작/중지 처리)
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.common.service;

import kr.co.kgc.point.batch.common.exception.ScheduleRequestException;
import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import kr.co.kgc.point.batch.domain.common.dto.ScheduleRequestDto;
import kr.co.kgc.point.batch.domain.common.dto.ScheduleResponseDto;
import kr.co.kgc.point.batch.domain.common.util.quartz.CronJobLauncher;
import kr.co.kgc.point.batch.domain.common.util.quartz.ScheduleCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jni.Local;
import org.quartz.SchedulerException;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ScheduleService {

    private static final Logger log = LogManager.getLogger();
    private final Scheduler scheduler;
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    private final ApplicationContext applicationContext;
    private final ScheduleCreator schedulerJobCreator;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final MessageSource messageSource;

    public ScheduleService(Scheduler scheduler, JobLauncher jobLauncher, JobLocator jobLocator,
                           ApplicationContext applicationContext, ScheduleCreator schedulerJobCreator,
                           JobOperator jobOperator, JobExplorer jobExplorer,
                           SchedulerFactoryBean schedulerFactoryBean, MessageSource messageSource) {
        this.scheduler = scheduler;
        this.jobLauncher = jobLauncher;
        this.jobLocator = jobLocator;
        this.applicationContext = applicationContext;
        this.schedulerJobCreator = schedulerJobCreator;
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.messageSource = messageSource;
    }

    /*
     * @method : createJobSchedule
     * @desc : Job Schedule을 등록해주는 메소드
     * @param : ScheduleRequestDto(jobName(Job Schedule 명), jobGroup(Job Schedule 그룹), startTime(스케쥴 시작시간),
     *                             cronExpression(크론 표현식), desc(Job Schedule 설명)
     * @return : ScheduleResponseDto
     * */
    public ScheduleResponseDto createJobSchedule(ScheduleRequestDto requestDto) {

        String jobClassName = CronJobLauncher.class.getName();
        String jobName = requestDto.getJobName();
        String jobGroup = requestDto.getJobGroup();
        String cronExpression = requestDto.getCronExpression();
        String desc = requestDto.getDesc();

        /* 필수값 체크 */
        if (CommonUtil.isEmpty(jobName) || CommonUtil.isEmpty(jobGroup) ||
                CommonUtil.isEmpty(cronExpression)) {
            log.error(">> Required value does not exist. jobGroup.jobName : {}, cronExpression : {}",
                    jobGroup + "." + jobName, cronExpression);
            throw new ScheduleRequestException("Required value does not exist. jobGroup.jobName : " +
                    jobGroup + "." + jobName + ", cronExpression : " + cronExpression);
        }

        LocalDateTime startTime = null;

        if (!CommonUtil.isEmpty(requestDto.getStartTime())) {
            startTime = LocalDateTime.parse(requestDto.getStartTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        } else {
            startTime = LocalDateTime.now();
        }

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobClassName))
                    .withIdentity(jobName, jobGroup)
                    .build();
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = schedulerJobCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobClassName),
                        false,
                        applicationContext,
                        jobName,
                        jobGroup,
                        desc);

                Trigger trigger = schedulerJobCreator.createCronTrigger(
                        jobName,
                        jobGroup,
                        startTime,
                        cronExpression);
                scheduler.scheduleJob(jobDetail, trigger);
                log.info(">> jobGroup.jobName : [" + jobGroup + "." +  jobName + "]" + " scheduled.");
            } else {
                log.error(">> Create Job Schedule Error. job Schedule Already Exist. jobGroup.jobName : {}",
                        jobGroup + "." + jobName);
                throw new ScheduleRequestException("Job Schedule Already Exist");
            }
        } catch (ClassNotFoundException e) {
            log.error(">> Class Not Found Error: jobClassName : {}, message : {}", jobClassName, e.getMessage());
            throw new ScheduleRequestException(e.getMessage());
        } catch (SchedulerException e) {
            log.error(">> Class Not Found Error: jobGroup.jobName : {}, message : {}",
                    jobGroup + "." + jobName, e.getMessage());
            throw new ScheduleRequestException(e.getMessage());
        }
        return new ScheduleResponseDto
                .Builder()
                .jobName(jobName)
                .jobGroup(jobGroup)
                .startTime(startTime)
                .cronExpression(cronExpression)
                .resultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /*
     * @method : createJobSchedule
     * @desc : Job Schedule의 cronExpression을 변경해주는 메소드
     * @param : ScheduleRequestDto(cronExpression(크론 표현식)), jobName(Job Schedule 명), jobGroup(Job Schedule 그룹)
     * @return : ScheduleResponseDto
     * */
    public ScheduleResponseDto updateJobSchedule(ScheduleRequestDto requestDto, String jobName, String jobGroup) {

        String cronExpression = requestDto.getCronExpression();
        Date nextStartTime = null; // 스케쥴 업데이트 결과값(다음 스케쥴 시작 시간)
        LocalDateTime startTime = null;    // 스케쥴 시작 시간
        if (!CommonUtil.isEmpty(requestDto.getStartTime())) {
            startTime = LocalDateTime.parse(requestDto.getStartTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }

        /* 필수값 체크 */
        if (CommonUtil.isEmpty(jobName) || CommonUtil.isEmpty(jobGroup) ||
                CommonUtil.isEmpty(cronExpression)) {
            log.error(">> Required value does not exist. jobGroup.jobName : {}, cronExpression : {}",
                    jobGroup + "." + jobName, cronExpression);
            throw new ScheduleRequestException("Required value does not exist. jobGroup.jobName : " +
                    jobGroup + "." + jobName + ", cronExpression : " + cronExpression);
        }

        /* 스케쥴러 정보 변경 */
        Trigger trigger = schedulerJobCreator.createCronTrigger(
                jobName,
                jobGroup,
                startTime,
                cronExpression);
        try {
            nextStartTime = schedulerFactoryBean.
                    getScheduler().
                    rescheduleJob(TriggerKey.triggerKey(jobName, jobGroup), trigger);
            log.info(">> job name : [" + jobGroup + "." + jobName +
                    "] updated and scheduled. nextStartTime : {}", nextStartTime);
        } catch (SchedulerException e) {
            log.error(">> Failed to update Job Schedule. jobGroup.jobName : {}, message : {}",
                    jobGroup + "." + jobName, e.getMessage());
            throw new ScheduleRequestException(e.getMessage());
        }

        if (CommonUtil.isEmpty(nextStartTime)) {   // 스케쥴러 정보 변경 실패 시
            log.error(">> Failed to update Job Schedule(update target job not found). jobGroup.jobName : {},",
                    jobGroup + "." + jobName);
            throw new ScheduleRequestException("update target job Schedule not found");
        }
        return new ScheduleResponseDto
                .Builder()
                .jobName(jobName)
                .jobGroup(jobGroup)
                .startTime(new Timestamp(nextStartTime.getTime()).toLocalDateTime())    // Date -> LocalDateTime
                .cronExpression(cronExpression)
                .resultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 등록된 Job 스케쥴링을 삭제하는 함수 */
    public ScheduleResponseDto deleteJobSchedule(String jobName, String jobGroup) {

        /* 필수값 체크 */
        if (CommonUtil.isEmpty(jobName) || CommonUtil.isEmpty(jobGroup)) {
            log.error(">> Required value does not exist. jobGroup.jobName : {}",
                    jobGroup + "." + jobName);
            throw new ScheduleRequestException("Required value does not exist. jobGroup.jobName : " +
                    jobGroup + "." + jobName);
        }

        try {
            boolean result = schedulerFactoryBean
                    .getScheduler()
                    .deleteJob(new JobKey(jobName, jobGroup));
            if (result) {
                log.info(">> job name : [" + jobGroup + "." + jobName + "] deleted");
            } else {
                log.error(">> Failed to delete Job Schedule. jobName : {}", jobName);
                throw new ScheduleRequestException("Delete target not found");
            }
        } catch (SchedulerException e) {
            log.error(">> Failed to delete Job Schedule. jobGroup.jobName : {}, message : {}",
                    jobGroup + "." + jobName, e.getMessage());
            throw new ScheduleRequestException(e.getMessage());
        }
        return new ScheduleResponseDto
                .Builder()
                .jobName(jobName)
                .jobGroup(jobGroup)
                .resultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 등록된 Job 스케쥴러를 즉시 실행시키는 메소드(중지된 상태여도 실행 가능(1회)) */
    public ScheduleResponseDto startJobSchedule(String jobName, String jobGroup) {

        LocalDateTime startTime = LocalDateTime.now();  // 스케쥴 시작시간(현재시간)

        /* 필수값 체크 */
        if (CommonUtil.isEmpty(jobName) || CommonUtil.isEmpty(jobGroup)) {
            log.error(">> Required value does not exist. jobGroup.jobName : {}",
                    jobGroup + "." + jobName);
            throw new ScheduleRequestException("Required value does not exist. jobGroup.jobName : " +
                    jobGroup + "." + jobName);
        }
        /* 스케쥴러 실행 */
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .triggerJob(new JobKey(jobName, jobGroup));
            log.info(">> jobGroup.jobName : [" + jobGroup + "." + jobName + "] started now");
        } catch (SchedulerException e) {
            log.error(">> Failed to start Job Schedule : jobGroup.jobName : {}, message : {}",
                    jobGroup + "." + jobName, e.getMessage());
            throw new ScheduleRequestException(e.getMessage());
        }
        return new ScheduleResponseDto
                .Builder()
                .jobName(jobName)
                .jobGroup(jobGroup)
                .startTime(startTime)
                .resultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 실행중인 Job 스케쥴러를 중지상태로 변경하는 메소드 */
    public ScheduleResponseDto stopJobSchedule(String jobName, String jobGroup) {

        Trigger trigger = null;

        /* 필수값 체크 */
        if (CommonUtil.isEmpty(jobName) || CommonUtil.isEmpty(jobGroup)) {
            log.error(">> Required value does not exist. jobGroup.jobName : {}",
                    jobGroup + "." + jobName);
            throw new ScheduleRequestException("Required value does not exist. jobGroup.jobName : " +
                    jobGroup + "." + jobName);
        }

        /* 스케쥴러 중지 */
        try {
            trigger = schedulerFactoryBean.getScheduler().getTrigger(TriggerKey.triggerKey(jobName, jobGroup));

            schedulerFactoryBean
                    .getScheduler()
                    .pauseJob(new JobKey(jobName, jobGroup));
            log.info(">> jobGroup.jobName : [" + jobGroup + "." + jobName + "] paused");
        } catch (SchedulerException e) {
            log.error(">> Failed to stop Job Schedule. jobGroup.jobName : {}, message : {}",
                    jobGroup + "." + jobName, e.getMessage());
            throw new ScheduleRequestException(e.getMessage());
        }
        return new ScheduleResponseDto
                .Builder()
                .jobName(jobName)
                .jobGroup(jobGroup)
                .startTime(new Timestamp(trigger.getNextFireTime().getTime()).toLocalDateTime())   // Date -> LocalDateTime
                .resultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 중지된 스케쥴의 상태를 실행 가능하게 변경하는 메소드 */
    public ScheduleResponseDto resumeJobSchdule(String jobName, String jobGroup) {
        Trigger trigger = null;

        /* 필수값 체크 */
        if (CommonUtil.isEmpty(jobName) || CommonUtil.isEmpty(jobGroup)) {
            log.error(">> Required value does not exist. jobGroup.jobName : {}",
                    jobGroup + "." + jobName);
            throw new ScheduleRequestException("Required value does not exist. jobGroup.jobName : " +
                    jobGroup + "." + jobName);
        }

        /* 스케쥴러 상태 변경( */
        try {
            trigger = schedulerFactoryBean.getScheduler().getTrigger(TriggerKey.triggerKey(jobName, jobGroup));

            schedulerFactoryBean
                    .getScheduler()
                    .resumeJob(new JobKey(jobName, jobGroup));
            log.info(">> job name : [" + jobGroup + "." + jobName + "] resumed");
        } catch (SchedulerException e) {
            log.error(">> Failed to resume Job Schedule. jobGroup.jobName : {}, message : {}",
                    jobGroup + "." + jobName, e.getMessage());
            throw new ScheduleRequestException(e.getMessage());
        }
        return new ScheduleResponseDto
                .Builder()
                .jobName(jobName)
                .jobGroup(jobGroup)
                .startTime(new Timestamp(trigger.getNextFireTime().getTime()).toLocalDateTime())   // Date -> LocalDateTime
                .resultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .resultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }
}