package kr.co.kgc.point.batch.domain.sample.writer;

import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import kr.co.kgc.point.batch.domain.pos.mapper.SamplePosMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager ="posTransactionManager")
public class SampleWriter2 implements ItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger();

    @Autowired
    private SamplePosMapper samplePosMapper;

    private String jobName;
    private StepExecution stepExecution;

    public void setParameterValues(final Map<String, Object> parameterValues) {
        if (!CommonUtil.isEmpty(parameterValues)) {
            this.jobName = (String) parameterValues.get("jobName");
            this.stepExecution = (StepExecution) parameterValues.get("stepExecution");
        }
    }

    @Override
    public void write(List<? extends Map<String, Object>> list) throws Exception {
        if (CommonUtil.isEmpty(stepExecution)) {
            log.debug(" stepExcecution not sent error. stepExecution is required value");
            throw new RuntimeException("stepExecution is required value");
        }

        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();

        int result = 0;
        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] >> sampleItemWriter2..start.....{}", list);
        if (!CommonUtil.isEmpty(list)) {
            try {
                result = samplePosMapper.updateSamplePosListData(list);
                if (result <= 0) {
                    log.error("> [" + jobExecutionId + "|" + stepExecutionId + "] >> SampleWriter2 update error");
                }
            } catch (Exception e) {
                log.error("> [" + jobExecutionId + "|" + stepExecutionId + "] >> SampleWriter2 exception occur : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] >> sampleItemWriter2..finished.....");
    }
}
