package com.joysuch.document.domain;


import com.sushengren.easyword.annotation.WordProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClassInfo {

    @WordProperty("班级")
    private String className;

    @WordProperty("人数")
    private Integer numberOfPeople;

    @WordProperty("平均分")
    private Double theAverageScore;

    @WordProperty("年级")
    private String grade;

    @WordProperty("排名")
    private Integer ranking;

    @WordProperty("班主任")
    private String classTeacher;

    @WordProperty("学生列表")
    private List<StudentInfo> studentList;

}