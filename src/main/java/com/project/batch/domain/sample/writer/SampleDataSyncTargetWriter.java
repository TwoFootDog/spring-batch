/*
 * @file : com.project.batch.domain.sample.writer.SampleDataSyncTargetWriter.java
 * @desc : 데이터 동기화 Target DB의 Table(SYNC_TARGET_TABLE)에 데이터를 적재해주는 클래스.
 *         Writer를 호출한 STEP과 DB가 다르기 때문에 @Transactional의 Propagation 옵션을 REQUIRES_NEW 로 셋팅
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.writer;

import com.project.batch.domain.common.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.StepExecution;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRES_NEW, transactionManager ="secondDbTransactionManager")
public class SampleDataSyncTargetWriter extends MyBatisBatchItemWriter<Map<String, Object>> {

    private static final Logger log = LogManager.getLogger();
    private String jobName;
    private StepExecution stepExecution;

    /*
     * @method : write
     * @desc : 데이터 동기화 Target DB의 Table(SYNC_TARGET_TABLE)에 데이터 적재
     * @param :
     * @return :
     * */
    @Override
    public void write(List<? extends Map<String, Object>> items) {
        if (CommonUtil.isEmpty(stepExecution)) {
            log.error("> stepExcecution 미전송 에러. stepExecution은 필수값입니다.");
            throw new RuntimeException("stepExecution은 필수값입니다.");
        }

        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();

        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] > sampleItemWriter..start.....{}", items);
        super.write(items);
        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] > sampleItemWriter..finished.....");
    }

    /*
     * @method : setParameterValues
     * @desc : SampleDataSyncTargetWriter 파라미터를 셋팅하는 함수
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
