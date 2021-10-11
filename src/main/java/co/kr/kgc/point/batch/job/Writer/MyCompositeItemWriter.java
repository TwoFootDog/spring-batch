package co.kr.kgc.point.batch.job.Writer;

import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyCompositeItemWriter extends CompositeItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger(MyCompositeItemWriter.class);

    @Autowired
    private SamplePosMapper samplePosMapper;
    private List<ItemWriter<? super Map<String, Object>>> delegates;

    @Override
    public void write(List<? extends Map<String, Object>> item) throws Exception {
        Iterator var2 = this.delegates.iterator();

        while(var2.hasNext()) {
            ItemWriter<? super Map<String, Object>> writer = (ItemWriter)var2.next();

            log.info(">>>>>>>>>>>>MyCompositItemWriter getclass getname >>>>>> " + writer.getClass().getName());
            log.info(">>>>>>>>>>>>MyCompositItemWriter getclass >>>>>> " + writer.getClass());
            try {
                writer.write(item);
            } catch (Exception e) {
//                e.printStackTrace();
                throw new Exception(e);
            }
        }

    }

    @Override
    public void setDelegates(List<ItemWriter<? super Map<String, Object>>> delegates) {
        this.delegates = delegates;
    }
}
