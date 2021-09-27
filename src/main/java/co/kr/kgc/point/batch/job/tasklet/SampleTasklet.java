package co.kr.kgc.point.batch.job.tasklet;

import co.kr.kgc.point.batch.mapper.SampleMapper;
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

@RequiredArgsConstructor
public class SampleTasklet implements Tasklet, StepExecutionListener {

    private static final Logger logger = LogManager.getLogger(SampleTasklet.class);
    private final SampleMapper sampleMapper;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("SampleTasklet Start..........");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("SampleTasklet finish..........");
        return null;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        logger.info("SampleTasklet Doing..........");
        logger.info("Sample Data : {}" , sampleMapper.getSampleData());
        return null;
    }
}
