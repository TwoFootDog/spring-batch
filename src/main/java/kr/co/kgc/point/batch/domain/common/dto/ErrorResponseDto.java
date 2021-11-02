package kr.co.kgc.point.batch.domain.common.dto;

public class ErrorResponseDto {
    private String resultCode;
    private String resultMessage;

    public static class Builder {
        private String resultCode;
        private String resultMessage;

        public Builder() {}

        public ErrorResponseDto.Builder resultCode(String resultCode) {
            this.resultCode = resultCode;
            return this;
        }
        public ErrorResponseDto.Builder resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }
        public ErrorResponseDto build() {
            ErrorResponseDto errorResponseDto =  new ErrorResponseDto();
            errorResponseDto.resultCode = resultCode;
            errorResponseDto.resultMessage = resultMessage;
            return errorResponseDto;
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
