package com.joysuch.document.domain;


import com.sushengren.easyword.annotation.WordProperty;
import com.sushengren.easyword.converters.PictureConverter;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.util.List;

@Data
@Builder
public class DemoData {

    @WordProperty("标题")
    private String title;

    @WordProperty("生成时间")
    private String generationTime;

    @WordProperty("学生数")
    private Integer numberOfStudents;

    @WordProperty("监考老师数")
    private Integer numberOfInvigilators;

    @WordProperty("评卷老师数")
    private Integer numberOfTeachers;

    @WordProperty("年度")
    private String year;

    @WordProperty("学期")
    private String semester;

    @WordProperty("考试时间")
    private String examinationTime;

    @WordProperty("科目")
    private String subject;

    @WordProperty(value = "Logo", converter = PictureConverter.class)
    private InputStream logo;

    @WordProperty("班级列表")
    private List<ClassInfo> classList;


}