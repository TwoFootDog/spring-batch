package kr.co.kgc.point.batch.domain.common.util.quartz;

import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;

/* Quartz Trigger 및 Job 을 생성해주는 공통 함수 */
@Component
public class ScheduleCreator {
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;

    public ScheduleCreator(JobLauncher jobLauncher,
                           JobLocator jobLocator) {
        this.jobLauncher = jobLauncher;
        this.jobLocator = jobLocator;
    }

    public CronTrigger createCronTrigger(String triggerName,
                                         String triggerGroup,
                                         LocalDateTime startTime,
                                         String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName(triggerName);
        factoryBean.setGroup(triggerGroup);

        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);  // misfire 된 것을 재 수행하지않음
        if (!CommonUtil.isEmpty(startTime)) {
            factoryBean.setStartTime(Timestamp.valueOf(startTime));
        }
        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return factoryBean.getObject();
    }

    public JobDetail createJob(Class<? extends QuartzJobBean> jobClass,
                               boolean isDurable,
                               ApplicationContext context,
                               String jobName,
                               String jobGroup,
                               String desc) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);
        factoryBean.setDescription(desc);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", jobName);
        jobDataMap.put("jobGroup", jobGroup);
        factoryBean.setJobDataMap(jobDataMap);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
