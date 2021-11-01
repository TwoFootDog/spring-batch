package kr.co.kgc.point.batch.domain.common.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class CommonJobListener implements JobExecutionListener {
    private static final Logger log = LogManager.getLogger();
    private final JobExplorer jobExplorer;
    private final MessageSource messageSource;

    public CommonJobListener(JobExplorer jobExplorer, MessageSource messageSource) {
        this.jobExplorer = jobExplorer;
        this.messageSource = messageSource;
    }

    /* Batch Job 시작 전 실행 */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long jobExecutionId = jobExecution.getId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());

        /* 동일한 실행 중인 경우 예외 처리 */
         if (jobExplorer.findRunningJobExecutions(jobName).size() > 1) {
             log.info(">> [" + jobExecutionId + "] " + " Job is already running");
            throw new RuntimeException("Job is already running: "+ jobExecution.getJobInstance().getJobName());
         }

        log.info(">> [" + jobExecutionId + "] "
                + "batch Job Start. "
                + "jobName : [" + jobName + "]. "
                + "jobExecutionId : ["  + jobExecutionId + "]. "
                + "startTime : [" + startTime + "]" );
    }

    /* Batch Job 완료 전 실행 */
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long jobExecutionId = jobExecution.getId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());
        String endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getEndTime());
        String exitCode = jobExecution.getExitStatus().getExitCode();
        String exitMessage = null;
        log.info(">> [" + jobExecutionId + "] "
                + "batch Job End. "
                + "jobName : [" + jobName + "]. "
                + "jobExecutionId : ["  + jobExecutionId + "]. "
                + "startTime : [" + startTime + "]. "
                + "endTime : [" + endTime + "]. "
                + "exitCode : [" + exitCode + "]");

        /* exit message setting */
        if ("COMPLETED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.status.completed.msg", new String[]{}, null);
        } else if ("STOPPED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.status.stopped.msg", new String[] {}, null);
        } else if ("FAILED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.status.failed.msg", new String[]{}, null);
        }
        jobExecution.setExitStatus(new ExitStatus(exitCode, exitMessage));
    }
}