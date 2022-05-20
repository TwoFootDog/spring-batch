/*
 * @file : com.project.batch.domain.common.controller.BatchController.java
 * @desc : Spring Batch Job 시작/종료 서비스를 호출하는 Controller
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.common.controller;

import com.project.batch.domain.common.dto.BatchJobMastReqDto;
import com.project.batch.domain.common.dto.BatchJobMastResDto;
import com.project.batch.domain.common.dto.BatchResponseDto;
import com.project.batch.domain.common.service.BatchJobService;
import com.project.batch.domain.common.service.BatchService;
import com.project.batch.domain.common.util.CommandLineExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/batch")
public class BatchController {
    private static final Logger log = LogManager.getLogger();
    private final BatchService batchService;
    private final BatchJobService batchJobService;

    public BatchController(BatchService batchService,
                           BatchJobService batchJobService) {
        this.batchService = batchService;
        this.batchJobService = batchJobService;
    }

    /*
     * @method : startJob
     * @desc : Batch Job 즉시 실행(Job Schedule에 미등록되어 있어도 실행 가능)
     * @param : jobName(실행 대상 배치 Job 이름)
     * @return : BatchResponseDto (jobExecutionId(Job 실행 ID), jobName(배치Job 이름), requestDate(요청일시),
     *                             resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고))
     * */
    @GetMapping(value = "/start")
    public BatchResponseDto startJob(@RequestParam("jobName") String jobName,
                                     @RequestParam(value = "args1", required = false) String args1,
                                     @RequestParam(value = "args2", required = false) String args2,
                                     @RequestParam(value = "args3", required = false) String args3) {
        //CommandLineExecutor.execute("java -jar spring-batch-executor-0.0.1-SNAPSHOT.jar --spring.batch.job.names=sample2Job requestDate=99 parameter1=1 parameter2=3");
        CommandLineExecutor.execute("java -jar C:\\workspace\\java\\spring-batch-executor\\build\\libs\\spring-batch-executor-0.0.1-SNAPSHOT.jar --spring.batch.job.names=sample2Job requestDate=62 parameter1=1 parameter2=3");
        return batchService.startJob(jobName, args1, args2, args3);
    }

    /*
     * @method : stopJob
     * @desc : Batch Job 즉시 중지. 중지하면 배치 상태가 STOPPED 상태로 종료됨
     *         (동일 jobExecutionId로 재수행 불가)
     * @param : jobExecutionId(Job 실행 ID)
     * @return : BatchResponseDto (jobExecutionId(Job 실행 ID), jobName(배치Job 이름), requestDate(요청일시),
     *                             resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고))
     * */
    @GetMapping("/stop")
    public BatchResponseDto stopJob(@RequestParam("jobExecutionId") long jobExecutionId) {
        return batchService.stopJob(jobExecutionId);
    }

    @GetMapping("/job")
    public List<BatchJobMastResDto> getBatchJobList(@RequestParam(value = "jobName", required = false) String jobName,
                                                    @RequestParam("length") int length,
                                                    @RequestParam("start") int start) {
        log.info("jobName >>>" + jobName);
        return batchJobService.selectBatchJobList(jobName, length, start);
    }

    @GetMapping("/job/{id}")
    public BatchJobMastResDto getBatchJobDetailByPk(@PathVariable("id") long id) {
        log.info("id >>>" + id);
        return batchJobService.selectBatchJobDetailByPk(id);
    }

    @PutMapping("/job/{id}")
    public int updateBatchJobDetail(@PathVariable("id") long id, @RequestBody BatchJobMastReqDto req) {
        log.info("id >>>" + id);
        log.info("req>>> " + req);
        return batchJobService.updateBatchJobDetail(id, req);
    }

    @DeleteMapping("/job/{id}")
    public int deleteBatchJob(@PathVariable("id") long id) {
        log.info("id >>>" + id);
        return batchJobService.deleteBatchJob(id);
    }
}
