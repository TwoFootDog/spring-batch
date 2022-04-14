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

import com.project.batch.domain.common.service.BatchService;
import com.project.batch.domain.common.dto.BatchResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
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
}
