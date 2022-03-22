/*
 * @file : kr.co.kgc.point.batch.domain.common.listener.CommonJobListener.java
 * @desc : Spring Batch 의 Job 메인 로직 실행 전/후 처리를 수행하는 리스너
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.common.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;

@Component
public class CommonJobListener implements JobExecutionListener {

    private static final Logger log = LogManager.getLogger();
    private final JobExplorer jobExplorer;
    private final MessageSource messageSource;
    @Autowired
    private JobRegistry jobRegistry;

    public CommonJobListener(JobExplorer jobExplorer, MessageSource messageSource) {
        this.jobExplorer = jobExplorer;
        this.messageSource = messageSource;
    }

    /*
     * @method : beforeJob
     * @desc : Spring Batch Job 메인 로직 시작 전 실행. 동일한 Job이 실행 중이면 예외처리(배치 미실행)
     * @param :
     * @return :
     * */
    @Override
    public void beforeJob(JobExecution jobExecution) {

        String jobName = jobExecution.getJobInstance().getJobName();
        long jobExecutionId = jobExecution.getId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());

        /* 동일한 Job 명을 가진 배치가 실행 중인 경우 예외 처리
           (beforeJob 메소드가 수행된 경우면 Job 이미 실행된 경우이므로 동일한 배치 수행 중이면 갯수는 2로 나옴) */
         if (jobExplorer.findRunningJobExecutions(jobName).size() > 1) {
             log.info(">> [" + jobExecutionId + "] " + " Job is already running");
             jobRegistry
            throw new RuntimeException("Job is already running: "+ jobExecution.getJobInstance().getJobName());
         }

        log.info(">> [" + jobExecutionId + "] " +
                "batch Job Start. " +
                "jobName : [" + jobName + "]. " +
                "jobExecutionId : ["  + jobExecutionId + "]. " +
                "startTime : [" + startTime + "]" );
    }

    /*
     * @method : afterJob
     * @desc : Spring Batch Job 완료 전 실행. Batch Job의 상태 등을 저장 후 로깅
     * @param :
     * @return :
     * */
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long jobExecutionId = jobExecution.getId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());
        String endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getEndTime());
        String exitCode = jobExecution.getExitStatus().getExitCode();
        String exitMessage = null;

        log.info(">> [" + jobExecutionId + "] " +
                "batch Job End. " +
                "jobName : [" + jobName + "]. " +
                "jobExecutionId : ["  + jobExecutionId + "]. " +
                "startTime : [" + startTime + "]. " +
                "endTime : [" + endTime + "]. " +
                "exitCode : [" + exitCode + "]");

        /* Batch Job의 처리 결과 상태 및 메시지 셋팅(BATCH_JOB_EXECUTION 테이블의 EXIT_CODE, EXIT_MESSAGE) */
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