package co.kr.kgc.point.batch.job.Writer;

import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.jsr.item.ItemWriterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

//@NoArgsConstructor
//@RequiredArgsConstructor
//@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SampleWriter2 extends MyBatisBatchItemWriter {
    private static final Logger log = LogManager.getLogger(SampleWriter2.class);

//    private final SamplePosMapper samplePosMapper;

    @Override
    public void setSqlSessionFactory(@Qualifier("posSessionFactory") SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public void setStatementId(String statementId) {
        super.setStatementId(statementId);
    }

    @Override
    public void write(List items) {
        super.write(items);
    }
}
