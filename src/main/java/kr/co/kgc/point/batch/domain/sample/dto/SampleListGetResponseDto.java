package kr.co.kgc.point.batch.domain.sample.dto;

import java.util.List;
import java.util.Map;

public class SampleListGetResponseDto {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    private List<Map<String, Object>> data;
    private Map<String, Object> support;

    public SampleListGetResponseDto() {}

    public SampleListGetResponseDto(int page, int per_page, int total, int total_pages, List<Map<String, Object>> data, Map<String, Object> support) {
        this.page = page;
        this.per_page = per_page;
        this.total = total;
        this.total_pages = total_pages;
        this.data = data;
        this.support = support;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Map<String, Object> getSupport() {
        return support;
    }

    public void setSupport(Map<String, Object> support) {
        this.support = support;
    }
}
