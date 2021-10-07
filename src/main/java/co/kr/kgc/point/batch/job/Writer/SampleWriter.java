package co.kr.kgc.point.batch.job.Writer;

import co.kr.kgc.point.batch.mapper.point.SampleMapper;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

//@Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "posTransactionManager")
@NoArgsConstructor
@Transactional(transactionManager = "posTransactionManager")
public class SampleWriter implements ItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger(SampleWriter.class);

    @Autowired
    private SamplePosMapper samplePosMapper;

    @Override
    public void write(List<? extends Map<String, Object>> list) throws Exception {
        log.info("SampleWriter list : " + list);
        for (int i = 0; i<10; i++) {
            try {
                Thread.sleep(1000);
                log.info(">>> sampleItemWriter..." + i + "second elapsed.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException();
//        int result = samplePosMapper.updateSamplePosListData(list);
//        log.info("SampleWriter result : " + result);
    }
}
