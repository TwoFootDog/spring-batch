package co.kr.kgc.point.batch.mapper.etc;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
//@Repository
public interface SampleEtcMapper {
    public Map<String, Object> getSampleEtcData();
}
