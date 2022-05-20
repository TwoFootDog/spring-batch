package com.project.batch.domain.common.mapper;


import com.project.batch.domain.common.dto.BatchJobMastReqDto;
import com.project.batch.domain.common.dto.BatchJobMastResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BatchJobMapper {
    public List<BatchJobMastResDto> selectBatchJobList(@Param("jobName") String jobName,
                                                       @Param("length") int length,
                                                       @Param("start") int start);

    public BatchJobMastResDto selectBatchJobDetailByPk(@Param("id") long id);

    public int updateBatchJobDetail(@Param("id") long id,
                                    @Param("req") BatchJobMastReqDto req);

    public int deleteBatchJob(@Param("id") long id);
}
