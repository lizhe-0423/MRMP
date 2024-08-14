package com.joysuch.document;

import com.joysuch.document.domain.*;
import com.sushengren.easyword.EasyWord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() throws IOException {
        // 初始化学生信息列表
        List<StudentInfo> studentList = new ArrayList<>();
        // 添加学生信息
        studentList.add(new StudentInfo("小明", "No00001", 280.0, 93.3, 1, 1, ""));
        studentList.add(new StudentInfo("小红", "No00002", 260.0, 86.6, 2, 2, ""));
        studentList.add(new StudentInfo("小花", "No00003", 270.0, 90.0, 3, 120, ""));
        studentList.add(new StudentInfo("小莉", "No00004", 250.0, 83.3, 4, 210, ""));
        studentList.add(new StudentInfo("托尼", "No00005", 241.0, 80.3, 5, 600, ""));

        // 初始化班级信息列表
        List<ClassInfo> classList = new ArrayList<>();
        // 添加班级信息
        classList.add(new ClassInfo("一年级一班", 50, 270.5, "一年级", 1, "温娟", studentList));
        classList.add(new ClassInfo("一年级二班", 60, 260.5, "一年级", 2, "张三", studentList));
        classList.add(new ClassInfo("一年级三班", 35, 280.5, "一年级", 3, "李四", studentList));
        classList.add(new ClassInfo("一年级四班", 56, 290.5, "一年级", 4, "王五", studentList));

        // 构建演示数据对象
        DemoData data = DemoData.builder()
                .title("2022年度期末考试成绩报告")
                .generationTime("2022-01-01")
                .numberOfStudents(1510)
                .numberOfInvigilators(157)
                .numberOfTeachers(157)
                .year("二零二二")
                .semester("第二学期")
                .examinationTime("2022-10-01 至 2022-10-02")
                .subject("语文、数学、英语")
                .classList(classList)
                .logo(Files.newInputStream(Paths.get("C:\\Users\\admin\\Desktop\\微服务课题\\图片\\logo.jpg")))
                .build();

        // 定义输出模板 和 输出文件
        File file = new File("D:\\寻息工作\\课题下载文件\\期末成绩报告模板.docx");
        FileOutputStream out = new FileOutputStream("D:\\寻息工作\\课题下载文件\\期末报告成绩-1.docx");
        // 使用EasyWord进行模板渲染并输出到文件
        EasyWord.of(file).doWrite(data).toOutputStream(out);
    }
}
