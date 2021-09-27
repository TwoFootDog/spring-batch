package co.kr.kgc.point.batch.job.Quartz;


import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.SchedulerRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
//@Transactional
@RequiredArgsConstructor
public class SchedulerJobService {

    private static final Logger logger = LogManager.getLogger(SchedulerJobService.class);
    private final Scheduler scheduler;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final ApplicationContext applicationContext;
    private final JobScheduleCreator jobScheduleCreator;

    /* Job 스케쥴링을 등록하거나 변경하는 함수(컨트롤러에서 호출) */
    public void saveOrUpdate(SchedulerJobInfo schedulerJobInfo) throws Exception {
        if (schedulerJobInfo.getCronExpression().length() > 0) {
            schedulerJobInfo.setJobClass(SimpleCronJob.class.getName());
            schedulerJobInfo.setCronJob(true);
        } else {
            schedulerJobInfo.setJobClass(SimpleJob.class.getName());
            schedulerJobInfo.setCronJob(false);
            schedulerJobInfo.setRepeatTime((long)1);
        }

        logger.info(">>>>>>>>>>>> schedulerJobInfo : " + schedulerJobInfo);
        if (StringUtils.isEmpty(schedulerJobInfo.getJobId())) {
            createScheduleJob(schedulerJobInfo);    // 스케쥴 job 신규 생성
            logger.info(">>>>>>>>>> job Name : " + schedulerJobInfo.getJobId() + " created");
        } else {
            updateScheduleJob(schedulerJobInfo);    // 스케쥴 job 변경
            createScheduleJob(schedulerJobInfo);    // 스케쥴 job 신규 생성
            logger.info(">>>>>>>>>> job Name : " + schedulerJobInfo.getJobId() + " updated");
        }

        schedulerJobInfo.setDesc("i am job number "  + schedulerJobInfo.getJobId());
        schedulerJobInfo.setInterfaceName("interface_" + schedulerJobInfo.getJobId());
        logger.info(">>>>>>>>>> job Name : " + schedulerJobInfo.getJobId() + " created");
    }

    /* Job 스케쥴링을 등록하는 함수 */
    private void createScheduleJob(SchedulerJobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                    .build();
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = jobScheduleCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, applicationContext,
                        jobInfo.getJobName(), jobInfo.getJobGroup());

                Trigger trigger;
                if (jobInfo.getCronJob()) {
                    trigger = jobScheduleCreator.createCronTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = jobScheduleCreator.createSimpleTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getRepeatTime(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
//                jobInfo.setJobStatus("SCHEDULED");
//                schedulerRepository.save(jobInfo);
                logger.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
            } else {
                logger.error("scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            logger.error("Class Not Found - {}", jobInfo.getJobClass(), e);
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /* Job 스케쥴링을 변경하는 함수 */
    private void updateScheduleJob(SchedulerJobInfo jobInfo) {
        Trigger trigger;
        if (jobInfo.getCronJob()) { // CronJob인 경우
            trigger = jobScheduleCreator.createCronTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {                    // SimpleJob인 경우
            trigger = jobScheduleCreator.createSimpleTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        try {
            schedulerFactoryBean.
                    getScheduler().
                    rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), trigger);
//            jobInfo.setJobStatus("UPDATED & SCHEDULED");
            logger.info(">>>job name : [" + jobInfo.getJobName() + "] + updated and scheduled");
        } catch (SchedulerException e) {
            logger.info("SchedulerException : " + e.getMessage());
        }
    }

    /* Job을 즉시 실행시키는 함수 */
    public boolean startJob(SchedulerJobInfo jobInfo) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            logger.info(">>>job name : [" + jobInfo.getJobName() + "] + scheduled and started now");
            return true;
        } catch (SchedulerException e) {
            logger.info("Failed to start Job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    /* Job을 즉시 중지시키는 함수 */
    public boolean stopJob(SchedulerJobInfo jobInfo) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            logger.info(">>>job name : [" + jobInfo.getJobName() + "] + paused");
            return true;
        } catch (SchedulerException e) {
            logger.info("Failed to stop Job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    /* 중지된 Job을 즉시 재실행하는 함수 */
    public boolean resumeJob(SchedulerJobInfo jobInfo) {
        try {
            schedulerFactoryBean
                    .getScheduler()
                    .resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            logger.info(">>>job name : [" + jobInfo.getJobName() + "] + resumed");
            return true;
        } catch (SchedulerException e) {
            logger.info("Failed to resume Job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    /* 등록된 Job 스케쥴링을 삭제하는 함수 */
    public boolean deleteJob(SchedulerJobInfo jobInfo) {
        try {
             schedulerFactoryBean
                     .getScheduler()
                     .deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            logger.info(">>>job name : [" + jobInfo.getJobName() + "] + deleted");
            return true;
        } catch (SchedulerException e) {
            logger.info("Failed to delete Job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }
}
