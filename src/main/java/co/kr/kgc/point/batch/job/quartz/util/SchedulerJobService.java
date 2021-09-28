package co.kr.kgc.point.batch.job.quartz.util;


import co.kr.kgc.point.batch.job.quartz.CronJobLauncher;
import co.kr.kgc.point.batch.job.quartz.SimpleJobLauncher;
import co.kr.kgc.point.batch.job.quartz.domain.SchedulerJobDto;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
//@Transactional
@RequiredArgsConstructor
public class SchedulerJobService {

    private static final Logger logger = LogManager.getLogger(SchedulerJobService.class);

    private final Scheduler scheduler;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final ApplicationContext applicationContext;
    private final SchedulerJobCreator schedulerJobCreator;

    /* Job 스케쥴링을 등록하거나 변경하는 함수(Controller 에서 호출) */
    public void saveOrUpdate(SchedulerJobDto schedulerJobDto) throws Exception {
        if (schedulerJobDto.getCronExpression().length() > 0) {
            schedulerJobDto.setJobClass(CronJobLauncher.class.getName());
            schedulerJobDto.setCronJob(true);
        } else {
            schedulerJobDto.setJobClass(SimpleJobLauncher.class.getName());
            schedulerJobDto.setCronJob(false);
            schedulerJobDto.setRepeatTime((long) 1);
        }

        if (StringUtils.isEmpty(schedulerJobDto.getJobId())) {
            createScheduleJob(schedulerJobDto);    // 스케쥴 job 신규 생성
            logger.info(">>>>>>>>>> job Name : " + schedulerJobDto.getJobId() + " created");
        } else {
            updateScheduleJob(schedulerJobDto);    // 스케쥴 job 변경
            logger.info(">>>>>>>>>> job Name : " + schedulerJobDto.getJobId() + " updated");
        }
    }

    /* Job 스케쥴링을 등록하는 함수 */
    private void createScheduleJob(SchedulerJobDto jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                    .build();
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = schedulerJobCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
                        false,
                        applicationContext,
                        jobInfo.getJobName(),
                        jobInfo.getJobGroup(),
                        jobInfo.getDesc());

                Trigger trigger;
                if (jobInfo.isCronJob()) {
                    trigger = schedulerJobCreator.createCronTrigger(
                            jobInfo.getJobName(),
                            jobInfo.getJobGroup(),
                            new Date(),
                            jobInfo.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = schedulerJobCreator.createSimpleTrigger(
                            jobInfo.getJobName(),
                            jobInfo.getJobGroup(),
                            new Date(),
                            jobInfo.getRepeatTime(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
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
    private void updateScheduleJob(SchedulerJobDto jobInfo) {
        Trigger trigger;
        if (jobInfo.isCronJob()) { // CronJob인 경우
            trigger = schedulerJobCreator.createCronTrigger(
                    jobInfo.getJobName(),
                    jobInfo.getJobGroup(),
                    new Date(),
                    jobInfo.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);    // 최초 misfire 된것은 실행하나, 이후 misfire된 것은 폐기됨.
        } else {    // SimpleJob인 경우
            trigger = schedulerJobCreator.createSimpleTrigger(
                    jobInfo.getJobName(),
                    jobInfo.getJobGroup(),
                    new Date(),
                    jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);    // 최초 misfire 된것은 실행하나, 이후 misfire된 것은 폐기됨.
        }
        try {
            schedulerFactoryBean.
                    getScheduler().
                    rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), trigger);
            logger.info(">>>job name : [" + jobInfo.getJobName() + "] + updated and scheduled");
        } catch (SchedulerException e) {
            logger.info("SchedulerException : " + e.getMessage());
        }
    }

    /* Job을 즉시 실행시키는 함수 */
    public boolean startJob(SchedulerJobDto jobInfo) {
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
    public boolean stopJob(SchedulerJobDto jobInfo) {
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
    public boolean resumeJob(SchedulerJobDto jobInfo) {
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
    public boolean deleteJob(SchedulerJobDto jobInfo) {
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
