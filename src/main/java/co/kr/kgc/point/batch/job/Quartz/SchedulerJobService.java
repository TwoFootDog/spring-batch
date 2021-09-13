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
@Transactional
@RequiredArgsConstructor
public class SchedulerJobService {

    private static final Logger logger = LogManager.getLogger(SchedulerJobService.class);
    private final Scheduler scheduler;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final SchedulerRepository schedulerRepository;
    private final ApplicationContext applicationContext;
    private final JobScheduleCreator jobScheduleCreator;

    public void saveOrUpdate(SchedulerJobInfo schedulerJobInfo) throws Exception {
        if (schedulerJobInfo.getCronExpression().length() > 0) {
            schedulerJobInfo.setJobClass(SimpleCronJob.class.getName());
            schedulerJobInfo.setCronJob(true);
        } else {
            schedulerJobInfo.setJobClass(SimpleJob.class.getName());
            schedulerJobInfo.setCronJob(false);
            schedulerJobInfo.setRepeatTime((long)1);
        }

        if (StringUtils.isEmpty(schedulerJobInfo.getJobId())) {
            createScheduleJob(schedulerJobInfo);    // 스케쥴 job 신규 생성
        } else {
            updateScheduleJob(schedulerJobInfo);    // 스케쥴 job 변경
        }

        schedulerJobInfo.setDesc("i am job number "  + schedulerJobInfo.getJobId());
        schedulerJobInfo.setInterfaceName("interface_" + schedulerJobInfo.getJobId());
        logger.info(">>>>>>>>>> job Name : " + schedulerJobInfo.getJobId() + " created");
    }

    private void createScheduleJob(SchedulerJobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
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
                jobInfo.setJobStatus("SCHEDULED");
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

    private void updateScheduleJob(SchedulerJobInfo schedulerJobInfo) {

    }

}
