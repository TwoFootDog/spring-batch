package co.kr.kgc.point.batch.common.util.quartz;


import co.kr.kgc.point.batch.common.util.CommonUtil;
import co.kr.kgc.point.batch.domain.ScheduleRequestDto;
import co.kr.kgc.point.batch.domain.ScheduleResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* ScheduleController에서 직접 호출해주는 Quartz 관련 서비스 */
@Service
public class ScheduleService {
    private static final Logger log = LogManager.getLogger(ScheduleService.class);

    private final Scheduler scheduler;
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    private final ApplicationContext applicationContext;
    private final ScheduleCreator schedulerJobCreator;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final MessageSource messageSource;

    public ScheduleService(Scheduler scheduler,
                           JobLauncher jobLauncher,
                           JobLocator jobLocator,
                           ApplicationContext applicationContext,
                           ScheduleCreator schedulerJobCreator,
                           JobOperator jobOperator,
                           JobExplorer jobExplorer,
                           SchedulerFactoryBean schedulerFactoryBean,
                           MessageSource messageSource) {
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

    /* Job 스케쥴링을 등록하는 함수 */
    public ScheduleResponseDto createJobSchedule(ScheduleRequestDto requestDto) {
        String jobClassName = CronJobLauncher.class.getName();

        Date startTime = null;
        try {
            if (!CommonUtil.isEmpty(requestDto.getStartTime())) {
                startTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(requestDto.getStartTime());
            } else {
                startTime = new Date();
            }
        } catch (ParseException e) {
            log.error(">> Failed to create Job Schedule : {}", requestDto.getJobName(), e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobClassName))
                    .withIdentity(requestDto.getJobName(), requestDto.getJobGroup())
                    .build();
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = schedulerJobCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobClassName),
                        false,
                        applicationContext,
                        requestDto.getJobName(),
                        requestDto.getJobGroup(),
                        requestDto.getDesc());

                Trigger trigger = schedulerJobCreator.createCronTrigger(
                        requestDto.getJobName(),
                        requestDto.getJobGroup(),
                        startTime,
                        requestDto.getCronExpression());
                scheduler.scheduleJob(jobDetail, trigger);
                log.info(">> jobName : [" + requestDto.getJobGroup() + "." +  requestDto.getJobName() + "]" + " scheduled.");
            } else {
                log.error(">> Create Job Schedule Error. job Schedule Already Exist");
                return new ScheduleResponseDto
                        .Builder()
                        .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                        .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{"Job Schedule Already Exist"}, null))
                        .build();
            }
        } catch (ClassNotFoundException e) {
            log.error(">> Class Not Found : {}", jobClassName, e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        } catch (SchedulerException e) {
            log.error(">> Failed to create Job Schedule : {}", requestDto.getJobName(), e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* Job 스케쥴링을 변경하는 함수 */
    public ScheduleResponseDto updateJobSchedule(ScheduleRequestDto requestDto, String jobName, String jobGroup) {
        log.info("> trigger key : {}", TriggerKey.triggerKey(jobName, jobGroup));

        Trigger trigger = schedulerJobCreator.createCronTrigger(
                jobName,
                jobGroup,
                null,
                requestDto.getCronExpression());
        try {
            schedulerFactoryBean.
                    getScheduler().
                    rescheduleJob(TriggerKey.triggerKey(jobName, jobGroup), trigger);
            log.info(">> job name : [" + jobGroup + "." + jobName + "] updated and scheduled");
        } catch (SchedulerException e) {
            log.error(">> Failed to update Job Schedule : {}", jobName, e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 등록된 Job 스케쥴링을 삭제하는 함수 */
    public ScheduleResponseDto deleteJobSchedule(String jobName, String jobGroup) {
        try {
            boolean result = schedulerFactoryBean
                    .getScheduler()
                    .deleteJob(new JobKey(jobName, jobGroup));
            if (result) {
                log.info(">> job name : [" + jobGroup + "." + jobName + "] deleted");
            } else {
                log.error(">> Failed to delete Job Schedule : {}", jobName);
                return new ScheduleResponseDto
                        .Builder()
                        .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                        .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{"Delete target not found"}, null))
                        .build();
            }
        } catch (SchedulerException e) {
            log.error(">> Failed to delete Job Schedule : {}", jobName, e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 등록된 Job 스케쥴러를 즉시 실행시키는 함수 */
    public ScheduleResponseDto startJobSchedule(String jobName, String jobGroup) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .triggerJob(new JobKey(jobName, jobGroup));
            log.info(">> job name : [" + jobGroup + "." + jobName + "] started now");
        } catch (SchedulerException e) {
            log.error(">> Failed to start Job Schedule : {}", jobGroup + "." + jobName, e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 실행중인 Job 스케쥴러를 즉시 중지시키는 함수 */
    public ScheduleResponseDto stopJobSchedule(String jobName, String jobGroup) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .pauseJob(new JobKey(jobName, jobGroup));
            log.info(">> job name : [" + jobGroup + "." + jobName + "] paused");
        } catch (SchedulerException e) {
            log.error(">> Failed to stop Job Schedule : {}", jobGroup + "." + jobName, e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }

    /* 중지된 Job을 즉시 재실행하는 함수 */
    public ScheduleResponseDto resumeJobSchdule(String jobName, String jobGroup) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .resumeJob(new JobKey(jobName, jobGroup));
            log.info(">> job name : [" + jobGroup + "." + jobName + "] resumed");
        } catch (SchedulerException e) {
            log.error(">> Failed to resume Job Schedule : {}", jobGroup + "." + jobName, e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.msg", new String[]{}, null))
                .build();
    }
}