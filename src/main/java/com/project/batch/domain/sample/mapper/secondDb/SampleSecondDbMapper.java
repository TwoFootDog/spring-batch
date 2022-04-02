/*
 * @file : com.project.batch.domain.point.mapper.SampleSecondDbMapper.java
 * @desc : Sample Mapper입니다
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.mapper.secondDb;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SampleSecondDbMapper {
    /*
     * @method : selectSamplePosData
     * @desc : 샘플 POS I/F 데이터 조회
     * @param :
     * @return : List(POS_IF_TABLE1 컬럼)
     * */
    public List<Map<String, Object>> selectSamplePosData();

    /*
     * @method : selectSamplePosData2
     * @desc : 샘플 POS 전체 건수 및 SEQ 조회
     * @param :
     * @return : Map(POS_IF_TABLE1의 PROC_DT가 NULL인 MIN/MAX POS_SEQ 및 건수)
     * */
    public Map<String, Object> selectSamplePosSeq();

    /*
     * @method : selectSamplePosSeq
     * @desc : 샘플 POS SEQ 최소/최대값으로 데이터 조회
     * @param : POS_IF_TABLE1의 MIN/MAX POS_SEQ
     * @return : Map(POS_IF_TABLE1 컬럼)
     * */
    public Map<String, Object> selectSamplePosData2(Map<String, Object> map);

    /*
     * @method : insertSamplePosData
     * @desc : 샘플 POS 데이터 등록
     * @param : POS_IF_TABLE1 데이터
     * @return :
     * */
    public int insertSamplePosData(Map<String, Object> map);

    /*
     * @method : updateSamplePosData
     * @desc : 샘플 POS 데이터 처리결과 단건 update
     * @param : MAP(POS_IF_TABLE1의 POS_SEQ, COLUMN1)
     * @return :
     * */
    public int updateSamplePosData(Map<String, Object> map);

    /*
     * @method : updateSamplePosDataList
     * @desc : 샘플 POS 데이터 처리결과 다건 update
     * @param : LIST(POS_IF_TABLE1의 POS_SEQ, COLUMN1)
     * @return :
     * */
    public int updateSamplePosDataList(List list);
}
