package co.kr.kgc.point.batch.job.Writer;

import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SampleCompositeItemWriter extends CompositeItemWriter<Map<String, Object>> implements SkipListener {
    private static final Logger log = LogManager.getLogger(SampleCompositeItemWriter.class);

    @Autowired
    private SamplePosMapper samplePosMapper;
    private List<ItemWriter<? super Map<String, Object>>> delegates;

    @Override
    public void write(List<? extends Map<String, Object>> item) throws Exception {
        Iterator var2 = this.delegates.iterator();

        log.info(">>>>>>>>>>>>SampleCompositItemWriter Start >>>>>> ");
        while(var2.hasNext()) {
            ItemWriter<? super Map<String, Object>> writer = (ItemWriter)var2.next();
            try {
                writer.write(item);

            } catch (DuplicateKeyException e) {
                if (item.size() == 1) { // 건건 COMMIT인 경우는 DUP KEY 에러 발생 시 무시
                    log.info(">>>>>>>>>>>>SampleCompositItemWriter size == 1 >>>>>> ");
                } else {
                    log.info(">>>>>>>>>>>>SampleCompositItemWriter size > 1 >>>>>> ");
                    throw new DuplicateKeyException(e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info(">>>>>>>>>>>>SampleCompositItemWriter exception occur >>>>>> ");
                throw new Exception(e);
            }
        }
    }

    @Override
    public void setDelegates(List<ItemWriter<? super Map<String, Object>>> delegates) {
        this.delegates = delegates;
        super.setDelegates(delegates);
    }

    @Override
    public void onSkipInRead(Throwable throwable) {
        log.info(">>>>>>>>>>>>MyCompositItemWriter read exception >>>>>> " + throwable);
    }

    @Override
    public void onSkipInWrite(Object o, Throwable throwable) {
        log.info(">>>>>>>>>>>>MyCompositItemWriter skip writer list >>>>>> {}", o);
        log.info(">>>>>>>>>>>>MyCompositItemWriter skip writer list >>>>>>" + o);
        log.info(">>>>>>>>>>>>MyCompositItemWriter write exception >>>>>> " + throwable);
    }

    @Override
    public void onSkipInProcess(Object o, Throwable throwable) {
        log.info(">>>>>>>>>>>>MyCompositItemWriter skip processor list >>>>>> {}", o);
        log.info(">>>>>>>>>>>>MyCompositItemWriter process exception >>>>>> " + throwable);
    }
}

