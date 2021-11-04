/*
 * @file : kr.co.kgc.point.batch.domain.sample.dto.SamplePostRequestDto.java
 * @desc : RestTemplate으로 외부 API POST 메소드 호출 단건 데이터 요청 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.dto;

public class SamplePostRequestDto {
    /* 외부 호출 응답 sample 컬럼1 */
    private String name;
    /* 외부 호출 응답 sample 컬럼2 */
    private String job;

    public SamplePostRequestDto(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public SamplePostRequestDto() {}

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

    public static class Builder {
        private String name;
        private String job;

        public Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder job(String job) {
            this.job = job;
            return this;
        }
        public SamplePostRequestDto build() {
            SamplePostRequestDto samplePostRequestDto = new SamplePostRequestDto();
            samplePostRequestDto.name = name;
            samplePostRequestDto.job = job;
            return samplePostRequestDto;
        }
    }
}
