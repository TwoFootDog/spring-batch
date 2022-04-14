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
     * @method : insertSyncTargetData
     * @desc : 데이터 등록
     * @param : SYNC_TARGET_TABLE 데이터
     * @return :
     * */
    public int insertSyncTargetData(Map<String, Object> map);

    /*
     * @method : updateSyncTargetData
     * @desc : 샘플 데이터 수정
     * @param : SYNC_TARGET_TABLE 데이터
     * @return :
     * */
    public int updateSyncTargetData(Map<String, Object> map);

    /*
     * @method : deleteSyncTargetData
     * @desc : 샘플 데이터 삭제
     * @param : SYNC_TARGET_TABLE 데이터
     * @return :
     * */
    public int deleteSyncTargetData(Map<String, Object> map);
}
