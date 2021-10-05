package co.kr.kgc.point.batch.job.tasklet.etc;

import co.kr.kgc.point.batch.mapper.etc.SampleEtcMapper;
import co.kr.kgc.point.batch.mapper.point.SampleMapper;
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

@RequiredArgsConstructor
public class SampleEtcTasklet implements Tasklet, StepExecutionListener {

    private static final Logger log = LogManager.getLogger(SampleEtcTasklet.class);
    private final SampleEtcMapper sampleEtcMapper;

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
        log.info("SampleEtcTasklet Doing..........");
        log.info("Sample Etc Data : {}" , sampleEtcMapper.getSampleEtcData());
        return null;
    }
}
