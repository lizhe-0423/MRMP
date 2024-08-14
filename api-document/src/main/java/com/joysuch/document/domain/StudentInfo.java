package com.joysuch.document.domain;


import com.sushengren.easyword.annotation.WordProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
    public  class StudentInfo {

        @WordProperty("姓名")
        private String name;

        @WordProperty("学号")
        private String studentID;

        @WordProperty("总分")
        private Double totalScore;

        @WordProperty("平均分")
        private Double theAverageScore;

        @WordProperty("排名")
        private Integer ranking;

        @WordProperty("年级排名")
        private Integer gradeRanking;

        @WordProperty("备注")
        private String remark;

    }
