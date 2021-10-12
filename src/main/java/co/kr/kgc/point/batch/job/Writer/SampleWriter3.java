package co.kr.kgc.point.batch.job.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public class SampleWriter3 extends MyBatisBatchItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger(SampleWriter3.class);

    @Override
    public void write(List<? extends Map<String, Object>> items) {
        log.info(">>> sampleItemWriter3..start..... {}");
        super.write(items);
        log.info(">>> sampleItemWriter3..finished.....");
    }
}
