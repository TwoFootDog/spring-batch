package co.kr.kgc.point.batch.common.util.quartz;


import co.kr.kgc.point.batch.domain.ScheduleResponseDto;
import co.kr.kgc.point.batch.job.quartz.eai.CronJobLauncher;
import co.kr.kgc.point.batch.job.quartz.eai.SimpleJobLauncher;
import co.kr.kgc.point.batch.domain.ScheduleRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.batch.core.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

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
        boolean isCronJob =
                (requestDto.getCronExpression() != null) ? true : false;
        String jobClassName =
                (isCronJob) ?
                CronJobLauncher.class.getName() :
                SimpleJobLauncher.class.getName();

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            log.info(">>>>> SchedulerRequestDto = [" + requestDto + "]" + ".");
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

                Trigger trigger;
                if (isCronJob) {
                    trigger = schedulerJobCreator.createCronTrigger(
                            requestDto.getJobName(),
                            requestDto.getJobGroup(),
                            new Date(),
                            requestDto.getCronExpression(),
                            CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);    // misfire 된 것을 재 수행하지않음
                } else {
                    trigger = schedulerJobCreator.createSimpleTrigger(
                            requestDto.getJobName(),
                            requestDto.getJobGroup(),
                            new Date(),
                            requestDto.getRepeatTime(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);// misfire 된 것을 재 수행하지않음
                }
                scheduler.scheduleJob(jobDetail, trigger);
                log.info(">>>>> jobName = [" + requestDto.getJobGroup() + "." +  requestDto.getJobName() + "]" + " scheduled.");
                log.info(">>>>> jobName = [" + requestDto.getJobGroup() + "." +  requestDto.getJobName() + "]" + " scheduled.");

            } else {
                log.error(">>>>> scheduleNewJobRequest. job Already Exist");
                return new ScheduleResponseDto
                        .Builder()
                        .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                        .setResultMessage(messageSource.getMessage("schedule.response.fail.message", new String[]{}, null))
                        .build();
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobClassName, e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.message", new String[]{}, null))
                    .build();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.message", new String[]{}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.message", new String[]{}, null))
                .build();
    }

    /* Job 스케쥴링을 변경하는 함수 */
    public ScheduleResponseDto updateJobSchedule(ScheduleRequestDto requestDto,
                                  String jobName,
                                  String jobGroup) {
        Trigger trigger;
        boolean isCronJob =
                (requestDto.getCronExpression() != null) ? true : false;

        log.info(">>>>>> trigger key : {}", TriggerKey.triggerKey(jobName, jobGroup));
        
        if (isCronJob) { // CronJob인 경우
            trigger = schedulerJobCreator.createCronTrigger(
                    jobName,
                    jobGroup,
                    new Date(),
                    requestDto.getCronExpression(),
                    CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);    // misfire 된 것을 재 수행하지않음
//                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);    // 최초 misfire 된것은 실행하나, 이후 misfire된 것은 폐기됨.
        } else {    // SimpleJob인 경우
            trigger = schedulerJobCreator.createSimpleTrigger(
                    jobName,
                    jobGroup,
                    new Date(),
                    requestDto.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);    // misfire 된 것을 재 수행하지않음
//                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);    // 최초 misfire 된것은 실행하나, 이후 misfire된 것은 폐기됨.
        }
        try {
            schedulerFactoryBean.
                    getScheduler().
                    rescheduleJob(TriggerKey.triggerKey(jobName, jobGroup), trigger);
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] + updated and scheduled");
        } catch (SchedulerException e) {
            log.info("SchedulerException : " + e.getMessage());
            return new ScheduleResponseDto
                    .Builder()
                    .setResultCode(messageSource.getMessage("schedule.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("schedule.response.fail.message", new String[]{}, null))
                    .build();
        }
        return new ScheduleResponseDto
                .Builder()
                .setResultCode(messageSource.getMessage("schedule.response.success.code", new String[]{}, null))
                .setResultMessage(messageSource.getMessage("schedule.response.success.message", new String[]{}, null))
                .build();
    }

    /* 등록된 Job 스케쥴링을 삭제하는 함수 */
    public boolean deleteJobSchedule(String jobName, String jobGroup) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .deleteJob(new JobKey(jobName, jobGroup));
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] + deleted");
            return true;
        } catch (SchedulerException e) {
            log.info("Failed to delete Job - {}", jobName, e);
            return false;
        }
    }

    /* 등록된 Job 스케쥴러를 즉시 실행시키는 함수 */
    public boolean startJobSchedule(String jobName, String jobGroup) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .triggerJob(new JobKey(jobName, jobGroup));
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] started now");
            return true;
        } catch (SchedulerException e) {
            log.info("Failed to start Job - {}", jobGroup + "." + jobName, e);
            return false;
        }
    }

    /* 실행중인 Job 스케쥴러를 즉시 중지시키는 함수 */
    public boolean stopJobSchedule(String jobName, String jobGroup) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .pauseJob(new JobKey(jobName, jobGroup));
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] paused");
            return true;
        } catch (SchedulerException e) {
            log.info("Failed to stop Job - {}", jobGroup + "." + jobName, e);
            return false;
        }
    }

    /* 중지된 Job을 즉시 재실행하는 함수 */
    public boolean resumeJobSchdule(String jobName, String jobGroup) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .resumeJob(new JobKey(jobName, jobGroup));
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] resumed");
            return true;
        } catch (SchedulerException e) {
            log.info("Failed to resume Job - {}", jobGroup + "." + jobName, e);
            return false;
        }
    }
}