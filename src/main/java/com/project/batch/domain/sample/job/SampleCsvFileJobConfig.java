/*
 * @file : com.project.batch.domain.sample.job.SampleCsvFileJobConfig.java
 * @desc : CSV File 을 읽은 후 CSV File을 생성하는 Batch Job / Step / Reader / Writer를 정의한 Job Config 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.job;

import com.project.batch.domain.common.listener.CommonJobListener;
import com.project.batch.domain.common.listener.CommonStepListener;
import com.project.batch.domain.sample.dto.SampleCsvReadDto;
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
public class SampleCsvFileJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager firstDbTransactionManager;

    private static final String READ_FILE_NAME = "csvFile/inputSample.csv";
    private static final String WRITE_FILE_NAME = "csvFile/outputSample.csv";
    private static final String[] COLUMN_NAME = {"id", "name", "value"};
    private static final String CSV_DELIMITER = ",";
    private static final int COLUMN_NAME_ROW = 1;
    private static final int CHUNK_SIZE = 100;

    public SampleCsvFileJobConfig(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  @Qualifier("firstDbTransactionManager") DataSourceTransactionManager firstDbTransactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.firstDbTransactionManager = firstDbTransactionManager;
    }

    /*
     * @method : sampleCsvFileJob
     * @desc : CSV File 을 읽은 후 CSV File을 생성하는 Batch Job. sampleFileProcessStep을 수행
     * @param : commonJobListener(공통 job 리스너), sampleFileProcessStep(파일 처리 Step)
     * @return :
     * */
    @Bean
    public Job sampleCsvFileJob(CommonJobListener commonJobListener,
                                @Qualifier("sampleCsvFileStep") Step sampleCsvFileStep) {
        return jobBuilderFactory.get("sampleCsvFileJob")
                .listener(commonJobListener)
                .preventRestart()           // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleCsvFileStep)
                .build();

    }

    /*
     * @method : sampleCsvFileStep
     * @desc : CSV File 을 읽은 후 CSV File을 생성하는 Batch Step. Chunk 방식으로 Reader/Writer 호출
     * @param : commonStepListener(공통 step 리스너)
     * @return :
     * */
    @Bean
    public Step sampleCsvFileStep(CommonStepListener commonStepListener) {
        return stepBuilderFactory.get("sampleCsvFileStep")
                .listener(commonStepListener)
                .transactionManager(firstDbTransactionManager)
                .<SampleCsvReadDto, SampleCsvReadDto>chunk(CHUNK_SIZE)
                .reader(sampleCsvFileReader())
                .writer(sampleCsvFileWriter())
                .build();
    }

    /*
     * @method : sampleCsvFileReader
     * @desc : CSV File 을 읽는 Reader. names에 정의된 Column을 targetType의 DTO클래스 형식으로 읽고,
     *         컬럼 간 구분은 delimiter에서 정의한 문자열로 구분
     * @param :
     * @return :
     * */
    @Bean
    public FlatFileItemReader<SampleCsvReadDto> sampleCsvFileReader() {
        return new FlatFileItemReaderBuilder<SampleCsvReadDto>()
                .name("sampleCsvFileReader")
                .resource(new FileSystemResource(READ_FILE_NAME))
                .delimited().delimiter(CSV_DELIMITER)
                .names(COLUMN_NAME)
                .targetType(SampleCsvReadDto.class)
                .linesToSkip(COLUMN_NAME_ROW) // 첫번째 row skip(컬렴명 skip)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy() {
                    @Override
                    public String postProcess(String recode) {
                        return recode.trim();
                    }
                })
                .build();
    }

    /*
     * @method : sampleCsvFileWriter
     * @desc : Reader를 통해 읽은 데이터를 CSV 파일로 생성하는 Writer
     * @param :
     * @return :
     * */
    @Bean
    public FlatFileItemWriter sampleCsvFileWriter() {
        BeanWrapperFieldExtractor<SampleCsvReadDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(COLUMN_NAME);
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<SampleCsvReadDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(CSV_DELIMITER);
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<SampleCsvReadDto> fileItemWriter = new FlatFileItemWriter<>();
        fileItemWriter.setResource(new FileSystemResource(WRITE_FILE_NAME));
        fileItemWriter.setAppendAllowed(true);  // file append 여부
        fileItemWriter.setLineAggregator(lineAggregator);

        return fileItemWriter;
    }
}
