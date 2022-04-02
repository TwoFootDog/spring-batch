/*
 * @file : com.project.batch.domain.sample.writer.SampleCompositeItemWriter.java
 * @desc : setDelegates 메소드를 통해 List로 유입된 Writer 들을 순서대로 실행시키는 Writer
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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.dao.DuplicateKeyException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SampleDataSyncCompositeWriter extends CompositeItemWriter<Map<String, Object>> {

    private static final Logger log = LogManager.getLogger();
    private List<ItemWriter<? super Map<String, Object>>> delegates;    // 실행시킬 Writer List
    private String jobName;
    private StepExecution stepExecution;

    /*
     * @method : write
     * @desc : delegates 내에 있는 ItemWriter 들을 Loop 돌리면서 수행(데이터 동기화 target DB 테이블에 데이터 적재 후
     *         source DB 테이블에 처리 결과 UPDATE 하는 Writer)
     *         target DB 테이블에 적재 시 STEP의 Commit-Interval 숫자만큼의 데이터가 적재 후 Commit이 일어나는데,
     *         대량 Commit이 일어날 때 DuplicateKeyException이 발생하게 되면, 다건 데이터 내에 중복되는 데이터가 있는 것이므로, 다시
     *         1건씩 적재 후 Commit이 일어나야 함
     *         때문에 item.size가 1보다 큰 경우(commit-interval의 숫자만큼 대량으로 Commit이 경우) DuplicateKeyException이 발생하면
     *         예외를 상위 Step으로 던지고, 상위 Step에서는 skipLimit() 와 skip() 메소드를 통해 예외를 무시하고, skip() 처리 될 경우
     *         1건씩 Write를 재 수행하며 중복된 데이터를 찾게 됨. 그러다가 DuplicateKeyException이 또 발생하게 되고, 그 에러는 Target
     *         Writer에서 무시하게 되면 그 다음 Source Writer 처리 결과를 Update하게 됨(중복난 경우 Target DB 에러는 무시, source DB
     *         에는 처리 완료로 Update)
     * @param :
     * @return :
     * */
    @Override
    public void write(List<? extends Map<String, Object>> item) throws Exception {

        if (CommonUtil.isEmpty(stepExecution)) {
            log.error("> stepExcecution 미전송 에러. stepExecution은 필수값입니다.");
            throw new RuntimeException("stepExecution은 필수값입니다.");
        }
        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();
        Iterator var2 = this.delegates.iterator();

        while(var2.hasNext()) {
            ItemWriter<? super Map<String, Object>> writer = (ItemWriter)var2.next();
            try {
                writer.write(item);
            } catch (DuplicateKeyException e) {
                if (item.size() == 1) { // 건건 COMMIT인 경우는 DUP KEY 에러 발생 시 무시
                    log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] " +
                            "데이터 중복 예외 발생(DuplicateKeyException). 건건 Commit인 경우 발생하였으므로 예외 무시(skip)");
                } else {
                    log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] 데이터 중복 예외 발생(DuplicateKeyException)" +
                            ". 대량 Commit인 경우 발생하였으므로 Step으로 Rollback");
                    throw new DuplicateKeyException(e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(e);
            }
        }
        log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] SampleCompositItemWriter End. Read Count : [" +
                stepExecution.getReadCount() + "]. Write Count :  [" +
                stepExecution.getWriteCount() + "]. Skip Count : [" +
                stepExecution.getSkipCount() + "]");
    }

    /*
     * @method : setDelegates
     * @desc : CompositeWriter에서 실행시킬 Writer List를 셋팅하는 메소드
     * @param :
     * @return :
     * */
    @Override
    public void setDelegates(List<ItemWriter<? super Map<String, Object>>> delegates) {
        this.delegates = delegates;
        super.setDelegates(delegates);
    }

    /*
     * @method : setParameterValues
     * @desc : CompositeWriter에 파라미터를 셋팅하는 함수
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

