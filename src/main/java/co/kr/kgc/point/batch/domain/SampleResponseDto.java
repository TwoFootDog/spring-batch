package co.kr.kgc.point.batch.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SampleResponseDto {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    private List<Map<String, Object>> data;
    private Map<String, Object> support;
}
