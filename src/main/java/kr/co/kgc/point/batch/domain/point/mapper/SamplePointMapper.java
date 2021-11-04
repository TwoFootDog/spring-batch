/*
 * @file : kr.co.kgc.point.batch.domain.point.mapper.SamplePointMapper.java
 * @desc : Sample
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.point.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SamplePointMapper {
    /*
     * @method : selectSampleData
     * @desc : select Sample 메소드
     * @param :
     * @return :
     * */
    public List<Map<String, Object>> selectSampleData();

    /*
     * @method : insertSampleData
     * @desc : Insert Sample 메소드
     * @param :
     * @return :
     * */
    public int insertSampleData(Map<String, Object> map);

    /*
     * @method : insertSampleDataList
     * @desc : Tasklet 테스트용 Sample
     * @param :
     * @return :
     * */
    public int insertSampleDataList(List list);
}
