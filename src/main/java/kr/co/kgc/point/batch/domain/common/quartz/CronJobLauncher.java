/*
 * @file : kr.co.kgc.point.batch.domain.common.util.quartz.CronJobLauncher.java
 * @desc : QUARTZ_CRON_TRIGGERS 테이블에 등록된 Quartz Schedule을 스케쥴 주기(cronExpression)에 맞춰서 실행시키는 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.common.util.quartz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CronJobLauncher extends QuartzJobBean {

    private static final Logger log = LogManager.getLogger();
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;
    @Autowired
    private JobExplorer jobExplorer;

    /*
     * @method : executeInternal
     * @desc : Quartz Schedule을 실행시키는 메소드
     * @param :
     * @return :
     * */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("--job.name", jobName)
                .addString("requestDate",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .toJobParameters();

        try {
            Job job = jobLocator.getJob(jobName);

            /* 동일한 Job 명을 가진 배치가 실행 중인 경우 미실행 */
            if (jobExplorer.findRunningJobExecutions(jobName).size() < 1) {
                JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                log.info(">> [" + jobExecution.getId() + "] Job Schedule start. " +
                         "jobName : [" + jobName + "]. " +
                         "jobExecutionId : [" + jobExecution.getId() + "]. ");
            }
        } catch (JobExecutionAlreadyRunningException
                | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException
                | NoSuchJobException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
