package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.common.util.batch.BatchService;
import co.kr.kgc.point.batch.domain.BatchResponseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
public class BatchController {

    private final BatchService batchService;
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    /* Batch Job 즉시 실행(Job Schedule에 미등록되어 있어도 실행 가능) */
    @GetMapping(value = "/start")
    public BatchResponseDto startJob(@RequestParam("jobName") String jobName) {
        return batchService.startJob(jobName);
    }

    @GetMapping("/stop")
    public BatchResponseDto stopJob(@RequestParam("jobExecutionId") long jobExecutionId) {
        return batchService.stopJob(jobExecutionId);
    }
}
