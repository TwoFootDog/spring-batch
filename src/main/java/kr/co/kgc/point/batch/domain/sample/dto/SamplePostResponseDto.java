/*
 * @file : kr.co.kgc.point.batch.domain.sample.dto.SamplePostResponseDto.java
 * @desc : RestTemplate으로 외부 API POST 메소드 호출 단건 데이터 응답 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.dto;

import java.util.Date;

public class SamplePostResponseDto {
    /* 외부 호출 응답 sample 컬럼1 */
    private String name;
    /* 외부 호출 응답 sample 컬럼2 */
    private String job;
    /* 외부 호출 응답 sample 컬럼3 */
    private int id;
    /* 외부 호출 응답 sample 컬럼4 */
    private Date createAt;

    public SamplePostResponseDto() {}

    public SamplePostResponseDto(String name, String job, int id, Date createAt) {
        this.name = name;
        this.job = job;
        this.id = id;
        this.createAt = createAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
