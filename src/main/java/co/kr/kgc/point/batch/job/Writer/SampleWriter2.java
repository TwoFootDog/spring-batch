package co.kr.kgc.point.batch.job.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;

import java.util.List;
import java.util.Map;


public class SampleWriter2 extends MyBatisBatchItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger(SampleWriter2.class);

    @Override
    public void write(List<? extends Map<String, Object>> items) {
        for (int i = 0; i<10; i++) {
            try {
                Thread.sleep(1000);
                log.info(">>> sampleItemWriter2..." + i + "second elapsed.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.write(items);
        log.info(">>> sampleItemWriter2..finished.....");
    }
}
