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
     * @method : selectSyncSourceDataSeq
     * @desc : 동기화 대상 MIN_SEQ/MAX_SEQ 및 건수 조회(100건 씩)
     * @param :
     * @return : Map<String, Object>
     * */
    public Map<String, Object> selectSyncSourceDataSeq();

    /*
     * @method : selectSyncSourceData
     * @desc : 동기화 대상 조회
     * @param : Map<String, Object>
     * @return : Map<String, Object>
     * */
    public Map<String, Object> selectSyncSourceData(Map<String, Object> map);

    /*
     * @method : updateSyncSourceData
     * @desc : 동기화 결과 업데이트
     * @param : Map<String, Object> map
     * @return : int
     * */
    public int updateSyncSourceData(Map<String, Object> map);

    /*
     * @method : selectSyncSourceDataList
     * @desc : 동기화 대상 조회
     * @param : Map<String, Object>
     * @return : List<Map<String, Object>>
     * */
    public List<Map<String, Object>> selectSyncSourceDataList();

    /*
     * @method : updateSyncSourceDataList
     * @desc : 동기화 처리결과 다건 update
     * @param : LIST
     * @return :
     * */
    public int updateSyncSourceDataList(List list);
}
