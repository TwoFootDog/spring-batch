package co.kr.kgc.point.batch.job.tasklet.point;

import co.kr.kgc.point.batch.mapper.point.SampleMapper;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SampleTasklet implements Tasklet, StepExecutionListener {

    private static final Logger log = LogManager.getLogger(SampleTasklet.class);
    private final SampleMapper sampleMapper;
    private final SamplePosMapper samplePosMapper;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("SampleTasklet Start..........");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("SampleTasklet finish..........");
        return null;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("SampleTasklet Doing..........");
        List<Map<String, Object>> list = null;
        try {
            list = samplePosMapper.selectSamplePosData();
            log.info("SampleTasklet list..........{}", list);
        } catch (Exception e) {
            e.printStackTrace();
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }
        int i = 0;
        for (Map<String, Object> inputMap : list) {
            log.info(">> samplePosMapper.insertSamplePosData input data: {}", inputMap);
            int result = sampleMapper.insertSampleData(inputMap);
            log.info(">> samplePosMapper.insertSamplePosData reesult data: {}",result);

            if ( result != 0 ) {
                Map<String, Object> inputPosMap = new HashMap<>();
                inputPosMap.put("column1", inputMap.get("column1"));
                int result2 = samplePosMapper.updateSamplePosData(inputPosMap);
            }
        }
        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }
}
