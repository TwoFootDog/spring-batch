/*
 * @file : kr.co.kgc.point.batch.domain.sample.writer.SampleCompositeItemWriter.java
 * @desc : setDelegates 메소드를 통해 List로 유입된 Writer 들을 순서대로 호출하는 Writer
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.dao.DuplicateKeyException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SampleDataSyncCompositeWriter extends CompositeItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger();

    private List<ItemWriter<? super Map<String, Object>>> delegates;

    @Override
    public void write(List<? extends Map<String, Object>> item) throws Exception {
        Iterator var2 = this.delegates.iterator();

        log.debug(">> SampleCompositItemWriter Start ");
        while(var2.hasNext()) {
            ItemWriter<? super Map<String, Object>> writer = (ItemWriter)var2.next();
            try {
                writer.write(item);
            } catch (DuplicateKeyException e) {
                if (item.size() == 1) { // 건건 COMMIT인 경우는 DUP KEY 에러 발생 시 무시
                    log.error(">> SampleCompositItemWriter size == 1");
                } else {
                    log.error(">> SampleCompositItemWriter size > 1");
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

