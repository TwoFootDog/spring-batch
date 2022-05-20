package com.project.batch.domain.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommonResDto<T> {
    /* 응답코드 */
    private String resultCode;
    /* 응답메시지 */
    private String resultMessage;

    private T data;
}
