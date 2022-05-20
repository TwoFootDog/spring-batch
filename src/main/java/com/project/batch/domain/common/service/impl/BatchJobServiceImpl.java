package com.project.batch.domain.common.service.impl;

import com.project.batch.domain.common.dto.BatchJobMastReqDto;
import com.project.batch.domain.common.dto.BatchJobMastResDto;
import com.project.batch.domain.common.mapper.BatchJobMapper;
import com.project.batch.domain.common.service.BatchJobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchJobServiceImpl implements BatchJobService {
    private static final Logger log = LogManager.getLogger();
    private final BatchJobMapper batchJobMapper;

    public BatchJobServiceImpl(BatchJobMapper batchJobMapper) {
        this.batchJobMapper = batchJobMapper;
    }

    public List<BatchJobMastResDto> selectBatchJobList(String jobName, int length, int start) {
        return batchJobMapper.selectBatchJobList(jobName, length, start);
    }

    public BatchJobMastResDto selectBatchJobDetailByPk(long id) {
        log.info("selectBatchJobDetailByPk id>>>" + id);
        BatchJobMastResDto resDto = batchJobMapper.selectBatchJobDetailByPk(id);
        log.info("resDto>>>" + resDto.getJobName());
        return batchJobMapper.selectBatchJobDetailByPk(id);
    }

    public int updateBatchJobDetail(long id, BatchJobMastReqDto req) {
        int result = batchJobMapper.updateBatchJobDetail(id, req);
        return result;
    }

    public int deleteBatchJob(long id) {
        int result = batchJobMapper.deleteBatchJob(id);
        return result;
    }

}
