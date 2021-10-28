package co.kr.kgc.point.batch.domain;

public class ScheduleResponseDto {
    private String resultCode;
    private String resultMessage;

    public static class Builder {
        private String resultCode;
        private String resultMessage;

        public Builder() {}

        public Builder setResultCode(String resultCode) {
            this.resultCode = resultCode;
            return this;
        }
        public Builder setResultMessage(String resultMessage) {
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
