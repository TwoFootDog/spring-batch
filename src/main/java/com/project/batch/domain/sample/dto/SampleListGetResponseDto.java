/*
 * @file : com.project.batch.domain.sample.dto.SampleListGetResponseDto.java
 * @desc : RestTemplate으로 외부 API 호출 다건 데이터 응답 DTO
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.dto;

import java.util.List;
import java.util.Map;

public class SampleListGetResponseDto {
    /* 외부 호출 응답 sample 컬럼1 */
    private int page;
    /* 외부 호출 응답 sample 컬럼2 */
    private int per_page;
    /* 외부 호출 응답 sample 컬럼3 */
    private int total;
    /* 외부 호출 응답 sample 컬럼4 */
    private int total_pages;
    /* 외부 호출 응답 sample List 컬럼1 */
    private List<Map<String, Object>> data;
    /* 외부 호출 응답 sample 컬럼5 */
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
