package co.kr.kgc.point.batch.mapper.point;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SamplePointMapper {
    public List<Map<String, Object>> selectSampleData();
    public int insertSampleData(Map<String, Object> map);
    public int insertSampleListData(List list);  // tasklet 테스트용
}
