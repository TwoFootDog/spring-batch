package kr.co.kgc.point.batch.domain.common.dto;

public class ScheduleResponseDto {
    private String resultCode;
    private String resultMessage;

    public static class Builder {
        private String resultCode;
        private String resultMessage;

        public Builder() {}

        public Builder resultCode(String resultCode) {
            this.resultCode = resultCode;
            return this;
        }
        public Builder resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }
        public ScheduleResponseDto build() {
            ScheduleResponseDto scheduleResponseDto =  new ScheduleResponseDto();
            scheduleResponseDto.resultCode = resultCode;
            scheduleResponseDto.resultMessage = resultMessage;
            return scheduleResponseDto;
        }
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
