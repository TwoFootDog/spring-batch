package kr.co.kgc.point.batch.domain.sample.writer;

import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.StepExecution;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRES_NEW, transactionManager ="pointTransactionManager")
public class SampleDataSyncTargetWriter extends MyBatisBatchItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger();

    private String jobName;
    private StepExecution stepExecution;

    public void setParameterValues(final Map<String, Object> parameterValues) {
        if (!CommonUtil.isEmpty(parameterValues)) {
            this.jobName = (String) parameterValues.get("jobName");
            this.stepExecution = (StepExecution) parameterValues.get("stepExecution");
        }
    }

    @Override
    public void write(List<? extends Map<String, Object>> items) {
        if (CommonUtil.isEmpty(stepExecution)) {
            log.debug(" stepExcecution not sent error. stepExecution is required value");
            throw new RuntimeException("stepExecution is required value");
        }

        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();

        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] >> sampleItemWriter..start.....{}", items);
        super.write(items);
        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] >> sampleItemWriter..finished.....");
    }
}
