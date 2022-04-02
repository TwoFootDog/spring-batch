/*
 * @file : com.project.batch.common.config.ContextRefreshEventListener.java
 * @desc : Spring이 기동 될 때마다 수행됨. 상태가 STARTED인 Job 전체 조회 후 상태를 FAILED 처리
 *         (Spring이 기동될 때 Job 상태가 STARTED인 경우는 배치가 수행 중에 Spring이 비정상 종료된 경우이며,
 *         상태가 STARTED인 경우 Job 재 수행 불가함)
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.common.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


@Component
public class ContextRefreshEventListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LogManager.getLogger();
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;

    public ContextRefreshEventListener(JobExplorer jobExplorer, JobRepository jobRepository) {
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
    }

    // Spring 이 기동될 때마다 수행됨. 상태가 STARTED인 Job 전체 조회 후 상태를 FAILD 처리
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Container Start. find STARTED STATUS Job and Change to FAILED");
        List<String> jobs = jobExplorer.getJobNames();
        for (String job : jobs) {
            Set<JobExecution> runningJobs = jobExplorer.findRunningJobExecutions(job);

            for (JobExecution runningJob : runningJobs) {
                try {
                    if (!runningJob.getStepExecutions().isEmpty()) {
                        Iterator<StepExecution> iter = runningJob.getStepExecutions().iterator();
                        while (iter.hasNext()) {
                            StepExecution runningStep = (StepExecution)iter.next();
                            if (runningStep.getStatus().isRunning()) {
                                runningStep.setEndTime(new Date());
                                runningStep.setStatus(BatchStatus.FAILED);
                                runningStep.setExitStatus(new ExitStatus("FAILED", "BATCH FAILED"));
                                jobRepository.update(runningStep);
                            }
                        }
                    }
                    runningJob.setEndTime(new Date());
                    runningJob.setStatus(BatchStatus.FAILED);
                    runningJob.setExitStatus(new ExitStatus("FAILED", "BATCH FAILED"));
                    jobRepository.update(runningJob);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
