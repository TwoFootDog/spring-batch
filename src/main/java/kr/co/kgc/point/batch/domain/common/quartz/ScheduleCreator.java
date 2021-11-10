/*
 * @file : kr.co.kgc.point.batch.domain.common.quartz.ScheduleCreator.java
 * @desc : Quartz Schedule의 Trigger 및 Job을 생성해주는 공통 메소드를 모아놓은 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.common.quartz;

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

@Component
public class ScheduleCreator {

    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;

    public ScheduleCreator(JobLauncher jobLauncher, JobLocator jobLocator) {
        this.jobLauncher = jobLauncher;
        this.jobLocator = jobLocator;
    }

    /*
     * @method : createCronTrigger
     * @desc : Quartz Schedule Trigger를 생성해주는 메소드
     * @param : triggerName(트리거 이름), triggerGroup(트리거 그룹), startTime(시작시간), cronExpression(스케줄 표현식)
     * @return :
     * */
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

    /*
     * @method : createJob
     * @desc : Quartz Schedule Trigger를 생성해주는 메소드
     * @param : jobClass(QuartzJobBean를 상속받은 Cron Job Launcher Class(util.quartz.CronJobLauncher),
     *          isDurable(true 면 한번 실행 후 Job 삭제. false 면 Job 삭제 안함), context(스프링 ApplicationContext),
     *          jobName(Quartz Schedule Job 이름), jobGroup(Quartz Schedule Job 그룹), desc(상세 설명)
     * @return : JobDetail
     * */
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
