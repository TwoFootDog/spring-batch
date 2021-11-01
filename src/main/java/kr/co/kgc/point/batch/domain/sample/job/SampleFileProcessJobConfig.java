package kr.co.kgc.point.batch.domain.sample.job;

import kr.co.kgc.point.batch.domain.common.listener.CommJobListener;
import kr.co.kgc.point.batch.domain.common.listener.CommStepListener;
import kr.co.kgc.point.batch.domain.sample.dto.SampleReadDto;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class SampleFileProcessJobConfig {
    private static final Logger log = LogManager.getLogger();

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;

    private static final String READ_FILE_NAME = "csvFile/inputSample.csv";
    private static final String WRITE_FILE_NAME = "csvFile/outputSample.csv";
    private static final String[] COLUMN_NAME = {"id", "name", "value"};
    private static final String CSV_DELIMITER = ",";
    private static final int COLUMN_NAME_ROW = 1;

    public SampleFileProcessJobConfig(JobBuilderFactory jobBuilderFactory,
                                      StepBuilderFactory stepBuilderFactory,
                                      @Qualifier("pointTransactionManager") DataSourceTransactionManager transactionManager,
                                      @Qualifier("pointSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.transactionManager = transactionManager;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Bean
    public Job sampleFileProcessJob(CommJobListener commJobListener,
                                    @Qualifier("sampleFileProcessStep") Step sampleFileProcessStep) {
        return jobBuilderFactory.get("sampleFileProcessJob")
                .listener(commJobListener)
                .preventRestart()
                .start(sampleFileProcessStep)
                .build();

    }

    @Bean
    public Step sampleFileProcessStep(CommStepListener commStepListener) {
        return stepBuilderFactory.get("sampleFileProcessStep")
                .listener(commStepListener)
                .transactionManager(transactionManager)
                .chunk(100)
                .reader(sampleFileItemReader())
                .writer(sampleFileItemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<SampleReadDto> sampleFileItemReader() {
        return new FlatFileItemReaderBuilder<SampleReadDto>()
                .name("sampleFileItemReader")
                .resource(new FileSystemResource(READ_FILE_NAME))
                .delimited().delimiter(CSV_DELIMITER)
                .names(COLUMN_NAME)
                .targetType(SampleReadDto.class)
                .linesToSkip(COLUMN_NAME_ROW) // 첫번째 row skip(컬렴명 skip)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy() {
                    @Override
                    public String postProcess(String recode) {
                        return recode.trim();
                    }
                })
                .build();
    }

    @Bean
    public FlatFileItemWriter sampleFileItemWriter() {
        BeanWrapperFieldExtractor<SampleReadDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(COLUMN_NAME);
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<SampleReadDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(CSV_DELIMITER);
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<SampleReadDto> fileItemWriter = new FlatFileItemWriter<>();
        fileItemWriter.setResource(new FileSystemResource(WRITE_FILE_NAME));
        fileItemWriter.setAppendAllowed(true);  // file append
        fileItemWriter.setLineAggregator(lineAggregator);

        return fileItemWriter;
    }
}
