package co.kr.kgc.point.batch.job.Writer;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SampleWriter implements ItemWriter<Map<String, Object>> {
    private static final Logger logger = LogManager.getLogger(SampleWriter.class);
    private Map<String, Object> parameterValues;

    public void setParameterValue(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public void write(List list) throws Exception {
        logger.info("SampleWriter .. List : {}..............", list);
    }
}
