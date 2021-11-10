/*
 * @file : kr.co.kgc.point.batch.domain.sample.job.SampleExcelFileJobConfig.java
 * @desc : Excel File 을 읽은 후 Excel File을 생성하는 Batch Job / Step / Reader / Writer를 정의한 Job Config 클래스
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
import kr.co.kgc.point.batch.domain.sample.dto.SampleExcelReadDto;
import kr.co.kgc.point.batch.domain.sample.writer.SampleExcelFileWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SampleExcelFileJobConfig {

    private static final Logger log = LogManager.getLogger();
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager pointTransacationManager;
    private static final String READ_FILE_NAME = "excelFile/inputSample.xlsx";
    private static final String WRITE_FILE_NAME = "excelFile/outputSample";
    private static final int CHUNK_SIZE = 100;
    private static final int COLUMN_NAME_ROW = 1;

    public SampleExcelFileJobConfig(JobBuilderFactory jobBuilderFactory,
                                    StepBuilderFactory stepBuilderFactory,
                                    @Qualifier("pointTransactionManager") DataSourceTransactionManager pointTransacationManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.pointTransacationManager = pointTransacationManager;
    }

    /*
     * @method : sampleExcelFileJob
     * @desc : Excel File 을 읽은 후 Excel File을 생성하는 Batch Job. sampleFileProcessStep을 수행
     * @param : commonJobListener(공통 job 리스너), sampleExcelFileStep(Excel 파일 처리 Step)
     * @return :
     * */
    @Bean
    public Job sampleExcelFileJob(@Qualifier("sampleExcelFileStep") Step sampleExcelFileStep,
                                  CommonJobListener commonJobListener) {
        return jobBuilderFactory.get("sampleExcelFileJob")
                .listener(commonJobListener)
                .preventRestart()
                .start(sampleExcelFileStep)
                .build();
    }

    /*
     * @method : sampleExcelFileStep
     * @desc : Excel File 을 읽은 후 Excel File을 생성하는 Batch Step. Chunk 방식으로 Reader/Writer 호출
     * @param : commonStepListener(공통 step 리스너), sampleExcelFileReader(파일 처리 Step)
     * @return :
     * */
    @Bean
    @JobScope
    public Step sampleExcelFileStep(CommonStepListener commonStepListener,
                                    PoiItemReader sampleExcelFileReader,
                                    SampleExcelFileWriter sampleExcelFileWriter) {
        return stepBuilderFactory.get("sampleExcelFileStep")
                .listener(commonStepListener)
                .<SampleExcelReadDto, SampleExcelReadDto>chunk(CHUNK_SIZE)
                .reader(sampleExcelFileReader)
                .writer(sampleExcelFileWriter)
                .build();
    }

    /*
     * @method : sampleExcelFileReader
     * @desc : Excel file의 내용을 읽어오는 Reader
     * @param :
     * @return :
     * */
    @Bean
    @StepScope
    public PoiItemReader<SampleExcelReadDto> sampleExcelFileReader() {
        PoiItemReader<SampleExcelReadDto> sampleExcelFileReader = new PoiItemReader<>();
        sampleExcelFileReader.setLinesToSkip(COLUMN_NAME_ROW);
        sampleExcelFileReader.setResource(new FileSystemResource(READ_FILE_NAME));
        sampleExcelFileReader.setRowMapper(excelRowMapper());
        return sampleExcelFileReader;
    }

    /*
     * @method : itemProcessor
     * @desc : reader를 통해 읽은 데이터의 필수값을 체크해주는 메소드(writer 수행 전 호출)
     * @param :
     * @return :
     * */
    @Bean
    @StepScope
    public ItemProcessor<SampleExcelReadDto, SampleExcelReadDto> itemProcessor(
            @Value("#{stepExecution}") StepExecution stepExecution) {
        return new ItemProcessor<SampleExcelReadDto, SampleExcelReadDto>() {
            long jobExecutionId = stepExecution.getJobExecutionId();
            @Override
            public SampleExcelReadDto process(SampleExcelReadDto sampleExcelReadDto) throws Exception {
                if (sampleExcelReadDto.getId().isEmpty()) {
                    log.error(">> [" + jobExecutionId + "] " + " id value of the excel file is required");
                    throw new RuntimeException("id value of the excel file is required");
                }
                return sampleExcelReadDto;
            }
        };
    }

    /*
     * @method : sampleExcelFileWriter
     * @desc : Reader를 통해 읽어온 내용을 Excel file에 기록하는 Writer. 파일은 Overwrite가 되지 않기 때문에, 파일명 뒤에 시간을 붙여서
     *         생성한다.
     * @param :
     * @return :
     * */
    @Bean
    @StepScope
    public SampleExcelFileWriter sampleExcelFileWriter(@Value("#{jobParameters[jobName]}") String jobName,
                                                       @Value("#{stepExecution}") StepExecution stepExecution) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(CHUNK_SIZE);
        SXSSFSheet sheets = workbook.createSheet("sheet1");
        String writeTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String writeFileName = WRITE_FILE_NAME + "_" + writeTime + ".xlsx";
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(writeFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SampleExcelFileWriter sampleExcelFileWriter = new SampleExcelFileWriter(sheets, workbook, fileOutputStream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("jobName", jobName);
        parameters.put("stepExecution", stepExecution);
        sampleExcelFileWriter.setParameterValues(parameters);
        return sampleExcelFileWriter;
    }

    /*
     * @method : excelRowMapper
     * @desc : reader에서 호출하는 메소드. 컬럼명으로 된 dto 클래스를 셋팅한다.
     * @param :
     * @return :
     * */
    private RowMapper<SampleExcelReadDto> excelRowMapper() {
        BeanWrapperRowMapper<SampleExcelReadDto> rowMapper = new BeanWrapperRowMapper<>();
        rowMapper.setTargetType(SampleExcelReadDto.class);
        return rowMapper;
    }
}
