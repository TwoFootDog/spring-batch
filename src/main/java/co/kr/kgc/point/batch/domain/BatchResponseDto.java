package co.kr.kgc.point.batch.domain;

public class BatchResponseDto {
    private long jobExecutionId;
    private String jobName;
    private String startTime;
    private String resultCode;
    private String resultMessage;

    public static class Builder {
        private long jobExecutionId;
        private String jobName;
        private String startTime;
        private String resultCode;
        private String resultMessage;

        public Builder() {}

        public Builder setJobExecutionId(long jobExecutionId) {
            this.jobExecutionId = jobExecutionId;
            return this;
        }

        public Builder setjobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setResultCode(String resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public Builder setResultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }
        public BatchResponseDto build() {
            BatchResponseDto batchResponseDto = new BatchResponseDto();
            batchResponseDto.jobExecutionId = jobExecutionId;
            batchResponseDto.jobName = jobName;
            batchResponseDto.startTime = startTime;
            batchResponseDto.resultCode = resultCode;
            batchResponseDto.resultMessage = resultMessage;
            return batchResponseDto;
        }
    }

 /*

     public BatchResponseDto(long jobExecutionId, String jobName, String startTime, String resultCode, String resultMessage) {
        this.jobExecutionId = jobExecutionId;
        this.jobName = jobName;
        this.startTime = startTime;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }*/
}
