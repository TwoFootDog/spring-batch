/*
 * @file : kr.co.kgc.point.batch.domain.common.dto.ErrorResponseDto.java
 * @desc : Spring Batch 서비스 및 Quartz Schedule 서비스 에러 응답 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.common.dto;

public class ErrorResponseDto {
    /* 응답코드 */
    private String resultCode;
    /* 응답메시지 */
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
