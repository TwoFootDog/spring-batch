package co.kr.kgc.point.batch.job.quartz.util;


import co.kr.kgc.point.batch.job.quartz.CronJobLauncher;
import co.kr.kgc.point.batch.job.quartz.SimpleJobLauncher;
import co.kr.kgc.point.batch.domain.SchedulerRequestDto;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.batch.core.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
//@Transactional
@RequiredArgsConstructor
public class SchedulerService {

    private static final Logger log = LogManager.getLogger(SchedulerService.class);

    private final Scheduler scheduler;
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final ApplicationContext applicationContext;
    private final SchedulerCreator schedulerJobCreator;

    /* Job 스케쥴링을 등록하거나 변경하는 함수(Controller 에서 호출) */
//    public void saveOrUpdate(SchedulerRequestDto requestDto) throws Exception {
//        if (requestDto.getCronExpression().length() > 0) {
//            requestDto.setJobClass(CronJobLauncher.class.getName());
//            requestDto.setCronJob(true);
//        } else {
//            requestDto.setJobClass(SimpleJobLauncher.class.getName());
//            requestDto.setCronJob(false);
//            requestDto.setRepeatTime((long) 1);
//        }
//
//        if (StringUtils.isEmpty(requestDto.getJobId())) {
//            createScheduleJob(requestDto);    // 스케쥴 job 신규 생성
//            logger.info(">>>>>>>>>> job Name : " + requestDto.getJobId() + " created");
//        } else {
//            updateScheduleJob(requestDto);    // 스케쥴 job 변경
//            logger.info(">>>>>>>>>> job Name : " + requestDto.getJobId() + " updated");
//        }
//    }

    /* Job 스케쥴링을 등록하는 함수 */
    public void createJobSchedule(SchedulerRequestDto requestDto) {
        boolean isCronJob = 
                (requestDto.getCronExpression().length() > 0) ? true : false;
        String jobClassName = 
                (requestDto.getCronExpression().length() > 0) ? 
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
                        requestDto.getJobGroup(),
                        requestDto.getJobName(),
                        requestDto.getDesc());

                Trigger trigger;
                if (isCronJob) {
                    trigger = schedulerJobCreator.createCronTrigger(
                            requestDto.getJobGroup(),
                            requestDto.getJobName(),
                            new Date(),
                            requestDto.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = schedulerJobCreator.createSimpleTrigger(
                            requestDto.getJobGroup(),
                            requestDto.getJobName(),
                            new Date(),
                            requestDto.getRepeatTime(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
                log.info(">>>>> jobName = [" + requestDto.getJobGroup() + "." +  requestDto.getJobName() + "]" + " scheduled.");
            } else {
                log.error(">>>>> scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobClassName, e);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    /* Job 스케쥴링을 변경하는 함수 */
    public void updateJobSchedule(SchedulerRequestDto requestDto,
                                  String jobGroup,
                                  String jobName) {
        Trigger trigger;
        boolean isCronJob =
                (requestDto.getCronExpression().length() > 0) ? true : false;

        log.info(">>>>>> trigger key : {}", TriggerKey.triggerKey(jobName, jobGroup));
        
        if (isCronJob) { // CronJob인 경우
            trigger = schedulerJobCreator.createCronTrigger(
                    jobGroup,
                    jobName,
                    new Date(),
                    requestDto.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);    // 최초 misfire 된것은 실행하나, 이후 misfire된 것은 폐기됨.
        } else {    // SimpleJob인 경우
            trigger = schedulerJobCreator.createSimpleTrigger(
                    jobGroup,
                    jobName,
                    new Date(),
                    requestDto.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);    // 최초 misfire 된것은 실행하나, 이후 misfire된 것은 폐기됨.
        }
        try {
            schedulerFactoryBean.
                    getScheduler().
                    rescheduleJob(TriggerKey.triggerKey(jobName, jobGroup), trigger);
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] + updated and scheduled");
        } catch (SchedulerException e) {
            log.info("SchedulerException : " + e.getMessage());
        }
    }

    /* 등록된 Job 스케쥴링을 삭제하는 함수 */
    public boolean deleteJobSchedule(String jobGroup, String jobName) {
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

    /* Job을 즉시 실행시키는 함수 */
    public boolean startJobSchedule(String jobGroup, String jobName) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .triggerJob(new JobKey(jobName, jobGroup));
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] + scheduled and started now");
            return true;
        } catch (SchedulerException e) {
            log.info("Failed to start Job - {}", jobGroup + "." + jobName, e);
            return false;
        }
    }

    /* Job을 즉시 중지시키는 함수 */
    public boolean stopJob(String jobGroup, String jobName) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .pauseJob(new JobKey(jobName, jobGroup));
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] + paused");
            return true;
        } catch (SchedulerException e) {
            log.info("Failed to stop Job - {}", jobGroup + "." + jobName, e);
            return false;
        }
    }

    /* 중지된 Job을 즉시 재실행하는 함수 */
    public boolean resumeJob(String jobGroup, String jobName) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .resumeJob(new JobKey(jobName, jobGroup));
            log.info(">>>>> job name : [" + jobGroup + "." + jobName + "] + resumed");
            return true;
        } catch (SchedulerException e) {
            log.info("Failed to resume Job - {}", jobGroup + "." + jobName, e);
            return false;
        }
    }

    /* Job을 즉시 실행시키는 함수 */
    public boolean startJob(String jobGroup, String jobName) {
        String requestDate = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        JobParameters jobParameters = new JobParametersBuilder()
                                            .addString("--job.name", jobName)
                                            .addString("requestDate", requestDate)
                                            .toJobParameters();
        try {
            Job job = jobLocator.getJob(jobName);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info(">>>>> Job Started : {} ", jobExecution);
            return true;
        } catch (NoSuchJobException | JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.info("Failed to start Job - {}", jobGroup + "." + jobName, e);
            return false;
        }
    }
}