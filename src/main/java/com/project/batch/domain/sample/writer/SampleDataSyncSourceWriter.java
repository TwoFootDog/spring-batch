/*
 * @file : com.project.batch.domain.sample.writer.SampleDataSyncSourceWriter.java
 * @desc : 데이터 동기화 Source DB의 Table(SYNC_SOURCE_TABLE)에 처리 결과를 UPDATE해주는 클래스. Writer에서 Mapper를 호출해줬기 때문에
 *         @Transactional의 propagation 옵션을 NOT_SUPPORTED로 해줘야 한다.
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.writer;

import com.project.batch.domain.common.util.CommonUtil;
import com.project.batch.domain.sample.mapper.firstDb.SampleFirstDbMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager ="firstDbTransactionManager")
public class SampleDataSyncSourceWriter implements ItemWriter<Map<String, Object>> {

    private static final Logger log = LogManager.getLogger();
    @Autowired
    private SampleFirstDbMapper sampleFirstDbMapper;
    private String jobName;
    private StepExecution stepExecution;

    /*
     * @method : write
     * @desc : 데이터 동기화 Source DB의 Table(SYNC_SOURCE_TABLE)에 동기화 결과를 Update
     * @param :
     * @return :
     * */
    @Override
    public void write(List<? extends Map<String, Object>> list) throws Exception {

        if (CommonUtil.isEmpty(stepExecution)) {
            log.error("> stepExcecution 미전송 에러. stepExecution은 필수값입니다.");
            throw new RuntimeException("stepExecution은 필수값입니다.");
        }

        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();
        int updateResult = 0;

        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] > sampleItemWriter2..start.....{}", list);
        if (!CommonUtil.isEmpty(list)) {
            try {
                updateResult = sampleFirstDbMapper.updateSyncSourceDataList(list);

                if (updateResult <= 0) {
                    log.error("> [" + jobExecutionId + "|" + stepExecutionId + "] > SampleWriter2 update error");
                }
            } catch (Exception e) {
                log.error("> [" + jobExecutionId + "|" + stepExecutionId + "] > SampleWriter2 exception occur : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] > sampleItemWriter2..finished.....");
    }

    /*
     * @method : setParameterValues
     * @desc : SampleDataSyncSourceWriter 파라미터를 셋팅하는 함수
     * @param :
     * @return :
     * */
    public void setParameterValues(final Map<String, Object> parameterValues) {
        if (!CommonUtil.isEmpty(parameterValues)) {
            this.jobName = (String) parameterValues.get("jobName");
            this.stepExecution = (StepExecution) parameterValues.get("stepExecution");
        }
    }
}
