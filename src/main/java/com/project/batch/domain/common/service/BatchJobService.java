package com.project.batch.domain.common.service;

import com.project.batch.domain.common.dto.BatchJobMastReqDto;
import com.project.batch.domain.common.dto.BatchJobMastResDto;

import java.util.List;

public interface BatchJobService {
    public List<BatchJobMastResDto> selectBatchJobList(String jobName, int length, int start);
    public BatchJobMastResDto selectBatchJobDetailByPk(long id);
    public int updateBatchJobDetail(long id, BatchJobMastReqDto req);
    public int deleteBatchJob(long id);
}
