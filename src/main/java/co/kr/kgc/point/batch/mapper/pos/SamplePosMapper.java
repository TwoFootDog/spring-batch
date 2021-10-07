package co.kr.kgc.point.batch.mapper.pos;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Mapper
//@Transactional
//@Repository
public interface SamplePosMapper {
    public List<Map<String, Object>> selectSamplePosData();
    public int insertSamplePosData(Map<String, Object> map);
    public int updateSamplePosData(Map<String, Object> map);
    public int updateSamplePosListData(List list);
}
