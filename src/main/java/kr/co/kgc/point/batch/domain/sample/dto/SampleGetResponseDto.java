/*
 * @file : kr.co.kgc.point.batch.domain.sample.dto.SampleGetResponseDto.java
 * @desc : RestTemplate으로 외부 API GET 메소드 호출 단건 데이터 응답 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.dto;

import java.util.Map;

public class SampleGetResponseDto {
    /* 외부 호출 응답 sample 컬럼1 */
    private Map<String, Object> data;
    /* 외부 호출 응답 sample 컬럼2 */
    private Map<String, Object> support;

    public SampleGetResponseDto() {}

    public SampleGetResponseDto(Map<String, Object> data, Map<String, Object> support) {
        this.data = data;
        this.support = support;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getSupport() {
        return support;
    }

    public void setSupport(Map<String, Object> support) {
        this.support = support;
    }
}
