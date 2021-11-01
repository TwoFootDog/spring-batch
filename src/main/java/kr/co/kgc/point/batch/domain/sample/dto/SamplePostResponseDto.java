package kr.co.kgc.point.batch.domain.sample.dto;

import java.util.Date;

public class SamplePostResponseDto {
    private String name;
    private String job;
    private int id;
    private Date createAt;

    public SamplePostResponseDto() {}

    public SamplePostResponseDto(String name, String job, int id, Date createAt) {
        this.name = name;
        this.job = job;
        this.id = id;
        this.createAt = createAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
