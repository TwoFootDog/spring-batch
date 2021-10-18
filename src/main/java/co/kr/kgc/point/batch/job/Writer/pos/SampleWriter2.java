package co.kr.kgc.point.batch.job.Writer.pos;

import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager ="posTransactionManager")
public class SampleWriter2 implements ItemWriter<Map<String, Object>> {
    private static final Logger log = LogManager.getLogger(SampleWriter2.class);

    @Autowired
    private SamplePosMapper samplePosMapper;

    /* 추가 */
    private SqlSessionTemplate sqlSessionTemplate;
    private Map<String, Object> parameterValues = null;
    private String jobName;

/*    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        if (this.sqlSessionTemplate == null) {
            this.sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
        }
    }*/

    public void setParameterValues(final Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
        if (this.parameterValues != null) {
            this.jobName = (String) this.parameterValues.get("jobName");
        }
    }
    /* 종료 */


    @Override
    public void write(List<? extends Map<String, Object>> list) throws Exception {
        int result = 0;
        if (!list.isEmpty()) {
            try {
                result = samplePosMapper.updateSamplePosListData(list);
            } catch (Exception e) {
                log.info("SampleWriter exception occur : " + e.getMessage());
                throw new Exception();
            }
            log.info("SampleWriter result : " + result);
        }
    }
}
