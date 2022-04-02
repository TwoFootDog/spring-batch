/*
 * @file : com.project.batch.domain.point.mapper.SampleFirstDbMapper.java
 * @desc : Sample Mapper입니다
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.mapper.firstDb;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SampleFirstDbMapper {
    /*
     * @method : selectSampleData
     * @desc : 샘플 포인트 데이터 조회
     * @param :
     * @return :
     * */
    public List<Map<String, Object>> selectSampleData();

    /*
     * @method : insertSampleData
     * @desc : 샘플 포인트 데이터 등록
     * @param : Map(POINT_TABLE1의 COLUMN1, COLUMN2)
     * @return :
     * */
    public int insertSampleData(Map<String, Object> map);

    /*
     * @method : insertSampleDataList
     * @desc : 샘플 포인트 데이터 대량 등록
     * @param : List(POINT_TABLE1의 COLUMN1, COLUMN2)
     * @return :
     * */
    public int insertSampleDataList(List list);
}
