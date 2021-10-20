package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.common.util.batch.BatchService;
import co.kr.kgc.point.batch.domain.BatchResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchController {

    private final BatchService batchService;
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    /* Batch Job 즉시 실행(Job Schedule에 미등록되어 있어도 실행 가능) */
    @GetMapping("/start")
    public BatchResponseDto startJob(@RequestParam("jobName") String jobName) {
        try {
            boolean result = batchService.startJob(jobName);
            if(!result) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @GetMapping("/stop")
    public String stopJob(@RequestParam("jobExecutionId") long jobExecutionId) {
        try {
            boolean result = batchService.stopJob(jobExecutionId);
            if(!result) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @GetMapping("/restart")
    public String restartJob(@RequestParam("jobExecutionId") long jobExecutionId) {
        try {
            boolean result = batchService.restartJob(jobExecutionId);
            if(!result) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
}
