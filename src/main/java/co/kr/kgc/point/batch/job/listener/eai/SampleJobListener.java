package co.kr.kgc.point.batch.job.listener.eai;

import lombok.RequiredArgsConstructor;
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
public class SampleJobListener implements JobExecutionListener {
    private static final Logger log = LogManager.getLogger(SampleJobListener.class);
    private final JobExplorer jobExplorer;
    private final MessageSource messageSource;

    public SampleJobListener(JobExplorer jobExplorer, MessageSource messageSource) {
        this.jobExplorer = jobExplorer;
        this.messageSource = messageSource;
    }

    /* Batch Job 시작 전 실행 */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long jobInstanceId = jobExecution.getJobInstance().getInstanceId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());

        /* 동일한 실행 중인 경우 예외 처리 */
         if (jobExplorer.findRunningJobExecutions(jobName).size() > 1) {
            throw new RuntimeException("Job is already running.: "+ jobExecution.getJobInstance().getJobName());
         }

        log.info(">> batch Job Start. " +
                "jobName : [" + jobName +
                "]. jobId : ["  + jobInstanceId +
                "]. startTime : [" + startTime + "]" );
    }

    /* Batch Job 완료 전 실행 */
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long jobInstanceId = jobExecution.getJobInstance().getInstanceId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());
        String endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getEndTime());
        String exitCode = jobExecution.getExitStatus().getExitCode();
        String exitMessage = null;

        log.info(">> batch Job End. " +
                "jobName : [" + jobName +
                "]. jobId : ["  + jobInstanceId +
                "]. startTime : [" + startTime +
                "]. endTime : [" + endTime +
                "]. exitCode : [" + exitCode + "]");

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
/*
public class EventCouponIssueJobListener implements JobExecutionListener {

    @Resource(name="commonDAO")
    private CommonDAO commonDAO;

    @Autowired
    private JobExplorer jobExplorer;

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    private boolean isJobRunning;

    private boolean existYn = false;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        JobLogger.info(jobName, "JobStartTime: "+jobExecution.getStartTime());
        JobLogger.info(jobName, "JobParameters: "+jobExecution.getJobParameters().getParameters());
        isJobRunning = false;
        if (jobExplorer.findRunningJobExecutions(jobExecution.getJobInstance().getJobName()).size() > 1) {
            isJobRunning = true;
            throw new RuntimeException("This job is already running.: "+ jobExecution.getJobInstance().getJobName());
        }

        JobLogger.info(jobName, "checking config EVENT_COUPON");

        String promoNo = commonDAO.getConfigVal("EVENT_COUPON_NO");

        if( !"".equals(ComUtil.NVL(promoNo)) ) {
            existYn = true;
        } else {
            throw new RuntimeException("config not exists !");
        }

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        String exitCode = jobExecution.getExitStatus().getExitCode();
        String exitMsg = null;
        if ("COMPLETED".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.processed", new String[]{}, null);
        }else if ("FAILED".equals(exitCode)) {
            if (isJobRunning) {
                exitMsg = messageSource.getMessage("batch.job.executing", new String[]{}, null);
            }else {
                if( !existYn ) {
                    exitCode = "STOPPED";
                    exitMsg = "프로모션 기간이 아닙니다.";
                }
            }
        }else if ("EXECUTING".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.executing", new String[]{}, null);
        }else if ("NOOP".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.noop", new String[]{}, null);
        }else if ("STOPPED".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.stopped", new String[]{}, null);
        }else {
            exitMsg = messageSource.getMessage("batch.job.processed", new String[]{}, null);
        }
        JobLogger.info(jobName, "JobEndTime: "+jobExecution.getEndTime());
        JobLogger.info(jobName, exitCode + ": " + exitMsg);
        if ("FAILED".equals(exitCode)) 	JobLogger.info(jobName, jobExecution.getExitStatus().getExitDescription());

        jobExecution.setExitStatus(new ExitStatus(exitCode, exitMsg));
    }

}*/

/*


public class AutoRefundPaStepListener implements StepExecutionListener {

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    @Resource(name = "commonDAO")
    private CommonDAO commonDAO;

    private static final Logger log = LoggerFactory.getLogger(AutoRefundPaStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();

        JobLogger.info(jobName, "Step: " + stepExecution.getStepName());
        JobLogger.info(jobName, "StartTime: " + stepExecution.getStartTime());

        try {
            Map<String, JobParameter> parameter = stepExecution.getJobParameters().getParameters();
            Map<String, Object> batchParameter = commonDAO.setDefaultParameter(parameter);

            batchParameter.put("jobName", jobName);
            batchParameter.put("reqUrl", PropertyUtil.getString("BACK_OFFICE_SERVER")+"/orderclaim/autorefund.do?");

            stepExecution.getExecutionContext().put("batchParameter", batchParameter);
            JobLogger.info(jobName, "batchParameter: " + batchParameter);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String exitCode = stepExecution.getExitStatus().getExitCode();
        String exitMsg = null;

        if ("COMPLETED".equals(exitCode)) {
            if (stepExecution.getReadCount() > 0) {
                if (stepExecution.getWriteSkipCount() > 0) {
                    exitMsg = messageSource.getMessage("batch.job.skipped",
                            new String[] { String.valueOf(stepExecution.getReadCount()), String.valueOf(stepExecution.getWriteCount()) }, null);
                } else {
                    exitMsg = messageSource.getMessage("batch.job.completed", new String[] { String.valueOf(stepExecution.getReadCount()) }, null);
                }
            } else {
                exitMsg = messageSource.getMessage("batch.job.nodata", new String[] {}, null);
            }
        } else if ("FAILED".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.failed", new String[] {}, null);
        } else if ("EXECUTING".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.executing", new String[] {}, null);
        } else if ("NOOP".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.noop", new String[] {}, null);
        } else if ("STOPPED".equals(exitCode)) {
            exitMsg = messageSource.getMessage("batch.job.stopped", new String[] {}, null);
        } else {
            exitMsg = messageSource.getMessage("batch.job.processed", new String[] {}, null);
        }

        JobLogger.info(jobName, "Total Count: " + stepExecution.getReadCount());
        JobLogger.info(jobName, "Success Count: " + stepExecution.getWriteCount());
        JobLogger.info(jobName, "Faild Count: " + stepExecution.getWriteSkipCount());
        JobLogger.info(jobName, exitCode + ": " + exitMsg);

        if ("FAILED".equals(exitCode))
            JobLogger.info(jobName, stepExecution.getExitStatus().getExitDescription());

        return new ExitStatus(exitCode, exitMsg);
    }

}
*/
