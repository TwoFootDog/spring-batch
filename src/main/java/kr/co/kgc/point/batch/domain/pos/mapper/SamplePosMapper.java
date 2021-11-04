/*
 * @file : kr.co.kgc.point.batch.domain.point.mapper.SamplePosMapper.java
 * @desc : Sample
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.pos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SamplePosMapper {
    /*
     * @method : selectSamplePosData
     * @desc : select Sample 메소드
     * @param :
     * @return :
     * */
    public List<Map<String, Object>> selectSamplePosData();

    /*
     * @method : selectSamplePosData2
     * @desc : select Sample 메소드2
     * @param :
     * @return :
     * */
    public Map<String, Object> selectSamplePosData2(Map<String, Object> map);

    /*
     * @method : selectSamplePosSeq
     * @desc : select Sample 메소드(seq 및 건수 조회)
     * @param :
     * @return :
     * */
    public Map<String, Object> selectSamplePosSeq();

    /*
     * @method : insertSamplePosData
     * @desc : select Sample 메소드
     * @param :
     * @return :
     * */
    public int insertSamplePosData(Map<String, Object> map);

    /*
     * @method : updateSamplePosData
     * @desc : Sample Data Update 메소드
     * @param :
     * @return :
     * */
    public int updateSamplePosData(Map<String, Object> map);

    /*
     * @method : updateSamplePosDataList
     * @desc : Sample Data List Update 메소드
     * @param :
     * @return :
     * */
    public int updateSamplePosDataList(List list);
}
