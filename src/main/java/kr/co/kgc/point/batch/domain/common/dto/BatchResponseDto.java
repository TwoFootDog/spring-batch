package kr.co.kgc.point.batch.domain.common.dto;

public class BatchResponseDto {
    private long jobExecutionId;
    private String jobName;
    private String requestDate;
    private String resultCode;
    private String resultMessage;

    public static class Builder {
        private long jobExecutionId;
        private String jobName;
        private String requestDate;
        private String resultCode;
        private String resultMessage;

        public Builder() {}

        public Builder jobExecutionId(long jobExecutionId) {
            this.jobExecutionId = jobExecutionId;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder requestDate(String requestDate) {
            this.requestDate = requestDate;
            return this;
        }

        public Builder resultCode(String resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public Builder resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }
        public BatchResponseDto build() {
            BatchResponseDto batchResponseDto = new BatchResponseDto();
            batchResponseDto.jobExecutionId = jobExecutionId;
            batchResponseDto.jobName = jobName;
            batchResponseDto.requestDate = requestDate;
            batchResponseDto.resultCode = resultCode;
            batchResponseDto.resultMessage = resultMessage;
            return batchResponseDto;
        }
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

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
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
    }
}
