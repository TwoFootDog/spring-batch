package kr.co.kgc.point.batch.common.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class ContextRefreshEventListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LogManager.getLogger();
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;
    private final JobOperator jobOperator;

    public ContextRefreshEventListener(JobExplorer jobExplorer, JobRepository jobRepository, JobOperator jobOperator) {
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
        this.jobOperator = jobOperator;
    }

    /*
     * @method : onApplicationEvent
     * @desc : Spring이 기동 될 때마다 수행됨. 상태가 STARTED인 Job 전체 조회 후 상태를 FAILED 처리
     *         (Spring이 기동될 때 Job 상태가 STARTED인 경우는 배치가 수행 중에 Spring이 비정상 종료된 경우이며,
     *          상태가 STARTED인 경우 Job 재 수행 불가함)
     * @param :
     * @return :
     * */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Container restart. find 'running' batch jobs and change STATUS");
        List<String> jobs = jobExplorer.getJobNames();
        for (String job : jobs) {
            Set<JobExecution> runningJobs = jobExplorer.findRunningJobExecutions(job);

            for (JobExecution runningJob : runningJobs) {
                try {
                    runningJob.setStatus(BatchStatus.FAILED);
                    runningJob.setEndTime(new Date());
                    jobRepository.update(runningJob);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
