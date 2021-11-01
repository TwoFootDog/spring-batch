package kr.co.kgc.point.batch.domain.sample.dto;

import java.util.Map;

public class SampleGetResponseDto {
    private Map<String, Object> data;
    private Map<String, Object> support;

    public SampleGetResponseDto() {}

    public SampleGetResponseDto(Map<String, Object> data, Map<String, Object> support) {
        this.data = data;
        this.support = support;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getSupport() {
        return support;
    }

    public void setSupport(Map<String, Object> support) {
        this.support = support;
    }
}
