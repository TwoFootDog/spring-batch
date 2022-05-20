package com.project.batch.domain.common.dto;

import java.util.Date;

public class BatchJobMastResDto {
    private long id;
    /* 배치 Job 이름 */
    private String jobName;
    /* 설명 */
    private String jobDesc;
    private Date insertDt;
    private Date updateDt;

    public static class Builder {
        private long id;
        private String jobName;
        private String jobDesc;
        private Date insertDt;
        private Date updateDt;

        public Builder() {}

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder jobDesc(String jobDesc) {
            this.jobDesc = jobDesc;
            return this;
        }

        public BatchJobMastResDto build() {
            BatchJobMastResDto batchJobResponseDto = new BatchJobMastResDto();
            batchJobResponseDto.id = id;
            batchJobResponseDto.jobName = jobName;
            batchJobResponseDto.jobDesc = jobDesc;
            return batchJobResponseDto;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public Date getInsertDt() {
        return insertDt;
    }

    public void setInsertDt(Date insertDt) {
        this.insertDt = insertDt;
    }

    public Date getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(Date updateDt) {
        this.updateDt = updateDt;
    }
}
