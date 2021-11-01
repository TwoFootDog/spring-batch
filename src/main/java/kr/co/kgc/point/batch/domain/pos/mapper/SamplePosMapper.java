package kr.co.kgc.point.batch.domain.pos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SamplePosMapper {
    public List<Map<String, Object>> selectSamplePosData();
    public Map<String, Object> selectSamplePosData2(Map<String, Object> map);
    public Map<String, Object> selectSamplePosSeq();
    public int insertSamplePosData(Map<String, Object> map);
    public int updateSamplePosData(Map<String, Object> map);
    public int updateSamplePosListData(List list);
}
