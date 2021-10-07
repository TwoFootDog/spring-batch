package co.kr.kgc.point.batch.job.Writer;

import co.kr.kgc.point.batch.mapper.point.SampleMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;

//@NoArgsConstructor
@RequiredArgsConstructor
public class SampleWriter implements ItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger(SampleWriter.class);

    private final SampleMapper sampleMapper;
//    private Map<String, Object> parameterValues;

//    public void setParameterValue(Map<String, Object> parameterValues) {
//        this.parameterValues = parameterValues;
//    }

    @Override
    public void write(List list) throws Exception {
        log.info("SampleWriter .. List : {}..............", list);
    }
}
