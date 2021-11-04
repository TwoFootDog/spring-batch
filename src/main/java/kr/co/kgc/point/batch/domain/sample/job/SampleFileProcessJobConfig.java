/*
 * @file : kr.co.kgc.point.batch.domain.sample.job.SampleFileProcessJobConfig.java
 * @desc : File 을 읽어서 배치 처리하는 Job / Step / Reader / Writer를 정의한 Job Config 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.job;

import kr.co.kgc.point.batch.domain.common.listener.CommonJobListener;
import kr.co.kgc.point.batch.domain.common.listener.CommonStepListener;
import kr.co.kgc.point.batch.domain.sample.dto.SampleReadDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
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

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager pointTransactionManager;

    private static final String READ_FILE_NAME = "csvFile/inputSample.csv";
    private static final String WRITE_FILE_NAME = "csvFile/outputSample.csv";
    private static final String[] COLUMN_NAME = {"id", "name", "value"};
    private static final String CSV_DELIMITER = ",";
    private static final int COLUMN_NAME_ROW = 1;

    public SampleFileProcessJobConfig(JobBuilderFactory jobBuilderFactory,
                                      StepBuilderFactory stepBuilderFactory,
                                      @Qualifier("pointTransactionManager") DataSourceTransactionManager pointTransactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.pointTransactionManager = pointTransactionManager;
    }

    /*
     * @method : sampleFileProcessJob
     * @desc : File 을 읽어서 배치 처리하는 Batch Job. sampleFileProcessStep을 수행
     * @param : commonJobListener(공통 job 리스너), sampleFileProcessStep(파일 처리 Step)
     * @return :
     * */
    @Bean
    public Job sampleFileProcessJob(CommonJobListener commonJobListener,
                                    @Qualifier("sampleFileProcessStep") Step sampleFileProcessStep) {
        return jobBuilderFactory.get("sampleFileProcessJob")
                .listener(commonJobListener)
                .preventRestart()           // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleFileProcessStep)
                .build();

    }

    /*
     * @method : sampleFileProcessStep
     * @desc : File 을 읽어서 처리하는 Batch Step. Chunk 방식으로 Reader/Writer 호출
     * @param : commonStepListener(공통 step 리스너)
     * @return :
     * */
    @Bean
    public Step sampleFileProcessStep(CommonStepListener commonStepListener) {
        return stepBuilderFactory.get("sampleFileProcessStep")
                .listener(commonStepListener)
                .transactionManager(pointTransactionManager)
                .chunk(100)
                .reader(sampleFileItemReader())
                .writer(sampleFileItemWriter())
                .build();
    }

    /*
     * @method : sampleFileItemReader
     * @desc : CSV File 을 읽는 Reader. names에 정의된 Column을 targetType의 DTO클래스 형식으로 읽고,
     *         컬럼 간 구분은 delimiter에서 정의한 문자열로 구분
     * @param :
     * @return :
     * */
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

    /*
     * @method : sampleFileItemWriter
     * @desc : Reader를 통해 읽은 데이터를 CSV 파일로 생성하는 Writer
     * @param :
     * @return :
     * */
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
        fileItemWriter.setAppendAllowed(true);  // file append 여부
        fileItemWriter.setLineAggregator(lineAggregator);

        return fileItemWriter;
    }
/*
    @Bean
    ItemReader<SampleReadDto> excelFileReader() {

    }l
*/
}
