package kr.co.kgc.point.batch.domain.point.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SamplePointMapper {
    public List<Map<String, Object>> selectSampleData();
    public int insertSampleData(Map<String, Object> map);
    public int insertSampleListData(List list);  // tasklet 테스트용
}
