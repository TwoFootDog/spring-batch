/*
 * @file : kr.co.kgc.point.batch.domain.sample.dto.SampleExcelReadDto.java
 * @desc : PoiItemReader(SampleExcelFileJobConfig) 에서 Excel 파일을 조회할 때 사용하는 DTO (Excel 컬럼과 동일)
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.dto;

public class SampleExcelReadDto {
    /* CSV sample 컬럼1 */
    private String id;
    /* CSV sample 컬럼2 */
    private String name;
    /* CSV sample 컬럼3 */
    private String value;

    public SampleExcelReadDto() {}

    public SampleExcelReadDto(String id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
