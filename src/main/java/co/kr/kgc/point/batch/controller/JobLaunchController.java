//package co.kr.kgc.point.batch.controller;
//
////import co.kr.kgc.point.kgcbatch.config.JobRepositoryConfig;
//import lombok.RequiredArgsConstructor;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.quartz.Scheduler;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@RequiredArgsConstructor
//@RestController
//public class JobLaunchController {
//
//    private final JobLauncher jobLauncher;
//    private final Job job;
////    private final SchedulerFactoryBean schedulerFactoryBean;
//    private static final Logger logger = LogManager.getLogger(JobLaunchController.class);
//
//    @GetMapping("/batch")
//    public String sampleJobStart(@RequestParam("jobName") String jobName)
//            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        String requestDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
////        Scheduler scheduler = schedulerFactoryBean.getScheduler();
////        scheduler.get
////        Job job =
//        JobParameters jobParameters = new JobParametersBuilder()
//                                            .addString("--job.name", jobName )
//                                            .addString("requestDate", requestDate)
//                                            .toJobParameters();
//
//        logger.info("jobName : {}", jobName );
//        logger.info("requestDate : {}", requestDate );
//
//        jobLauncher.run(job, jobParameters);
//        return "complete";
//    }
//}
