package co.kr.kgc.point.batch.job.Writer.composite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.dao.DuplicateKeyException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SampleCompositeItemWriter extends CompositeItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger();

    private List<ItemWriter<? super Map<String, Object>>> delegates;

    @Override
    public void write(List<? extends Map<String, Object>> item) throws Exception {
        Iterator var2 = this.delegates.iterator();

        log.info(">> SampleCompositItemWriter Start ");
        while(var2.hasNext()) {
            ItemWriter<? super Map<String, Object>> writer = (ItemWriter)var2.next();
            try {
                writer.write(item);
            } catch (DuplicateKeyException e) {
                if (item.size() == 1) { // 건건 COMMIT인 경우는 DUP KEY 에러 발생 시 무시
                    log.info(">> SampleCompositItemWriter size == 1");
                } else {
                    log.info(">> SampleCompositItemWriter size > 1");
                    throw new DuplicateKeyException(e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(e);
            }
        }
    }

    @Override
    public void setDelegates(List<ItemWriter<? super Map<String, Object>>> delegates) {
        this.delegates = delegates;
        super.setDelegates(delegates);
    }
}

