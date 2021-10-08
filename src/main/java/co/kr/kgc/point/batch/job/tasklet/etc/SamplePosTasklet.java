package co.kr.kgc.point.batch.job.tasklet.etc;

import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import com.mchange.v2.lang.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SamplePosTasklet implements Tasklet, StepExecutionListener {

    private static final Logger log = LogManager.getLogger(SamplePosTasklet.class);
    private final SamplePosMapper samplePosMapper;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("SampleEtcTasklet Start..........");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("SampleEtcTasklet finish..........");
        return null;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("SamplePosTasklet Doing..........");
        String startTime = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis());
        String endTime = null;

        for (int i = 1; i<=10000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("column1", i);
            map.put("column2", i);
            int result = samplePosMapper.insertSamplePosData(map);
            log.info("Sample Pos Data : " + i + "번째 데이터 결과 : " + result);

        }
        endTime = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis());
        log.info("Sample Pos Batch End. StartTime : "  + startTime, ", EndTime : " + endTime);
        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
//        return null;
    }
}
