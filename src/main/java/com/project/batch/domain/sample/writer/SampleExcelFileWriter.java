package com.project.batch.domain.sample.writer;

import com.project.batch.domain.common.util.CommonUtil;
import com.project.batch.domain.sample.dto.SampleExcelReadDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class SampleExcelFileWriter implements ItemWriter<SampleExcelReadDto> {

    private static final Logger log = LogManager.getLogger();
    private final Sheet sheet;
    private final SXSSFWorkbook workbook;
    private final FileOutputStream fileOutputStream;
    private String jobName;
    private StepExecution stepExecution;

    public SampleExcelFileWriter(Sheet sheet,
                                 SXSSFWorkbook workbook,
                                 FileOutputStream fileOutputStream) {
        this.sheet = sheet;
        this.workbook = workbook;
        this.fileOutputStream = fileOutputStream;
    }

    @Override
    public void write(List<? extends SampleExcelReadDto> list) throws Exception {

        if (CommonUtil.isEmpty(stepExecution)) {
            log.error("> stepExcecution 미전송 에러. stepExecution은 필수값입니다.");
            throw new RuntimeException("stepExecution은 필수값입니다.");
        }

        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();


        for (int i = 0; i<list.size(); i++) {
            writeRow(i, list.get(i));
        }
        try {
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.dispose();
        } catch (IOException e) {
            log.error("> [" + jobExecutionId + "|" + stepExecutionId +
                    "] Excel file write Exception : {}", e.getMessage());
        }
    }

    private void writeRow(int currentRowNumber, SampleExcelReadDto sampleExcelReadDto) {
        List<String> columns = asList(sampleExcelReadDto.getId(), sampleExcelReadDto.getName(), sampleExcelReadDto.getValue());
        Row row = this.sheet.createRow(currentRowNumber);
        for (int i = 0; i<columns.size(); i++) {
            writeCell(row, i, columns.get(i));
        }
    }

    private void writeCell(Row row, int currentColumnNumber, String value) {
        Cell cell = row.createCell(currentColumnNumber);
        cell.setCellValue(value);
    }

    /*
     * @method : setParameterValues
     * @desc : SampleDataSyncTargetWriter 파라미터를 셋팅하는 함수
     * @param :
     * @return :
     * */
    public void setParameterValues(final Map<String, Object> parameterValues) {
        if (!CommonUtil.isEmpty(parameterValues)) {
            this.jobName = (String) parameterValues.get("jobName");
            this.stepExecution = (StepExecution) parameterValues.get("stepExecution");
        }
    }
}
