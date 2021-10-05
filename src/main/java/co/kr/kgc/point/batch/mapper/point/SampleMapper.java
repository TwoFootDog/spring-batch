package co.kr.kgc.point.batch.mapper.point;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
//@Repository
public interface SampleMapper {
    public Map<String, Object> getSampleData();
}
