package kr.co.kgc.point.batch.domain.sample.dto;

public class SamplePostRequestDto {
    private String name;
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
