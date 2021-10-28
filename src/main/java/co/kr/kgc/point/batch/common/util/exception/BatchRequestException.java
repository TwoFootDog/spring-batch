package co.kr.kgc.point.batch.common.util.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class BatchRequestException extends RuntimeException {
    private String code;           // 응답 코드 번호 (>= 0 : 정상, < 0 비정상)
    private String logMessage;      // 로그메시지
    private Exception e;

    public BatchRequestException(String code, String logMessage) {
        this.code = code;
        this.logMessage = logMessage;
    }
}
