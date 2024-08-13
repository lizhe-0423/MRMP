package com.joysuch.document.manager;

import org.springframework.stereotype.Component;

import java.io.*;

/**
 * DocManager
 *
 * @author lizhe@joysuch.com
 * @version 1.0
 * {@code @description} 接口文档导出功能
 * {@code @date} 2024/8/13 下午3:19
 */
@Component
public class DocManager {
    /**
     * 表示执行命令后的结果记录类
     */
    private static class Result {
        private final Process process;
        private final BufferedReader reader;

        public Result(Process process, BufferedReader reader) {
            this.process = process;
            this.reader = reader;
        }
    }


    /**
     * 执行Java文档生成命令
     * 该方法负责生成Java文档，通过指定的命令行工具来实现
     *
     * @param projectDir 项目目录路径，用于定位到具体的源码位置
     * @throws IOException          当命令行执行发生IO错误时抛出
     * @throws InterruptedException 当等待命令行进程完成时线程被中断抛出
     */
    public void doGenerate(String projectDir, String outputDir, String filePath) throws IOException, InterruptedException {
        // 定义Windows系统下生成Java文档的命令
        // 这里指定了文档生成的编码、字符集以及输出目录，确保文档能正确生成

        outputDir = " C:\\Users\\admin\\Desktop\\微服务课题\\下载文件\\接口文档";
        filePath = " src/main/java/com/joysuch/apiuser/controller/UserController.java\n";
        String winMavenCommand = "javadoc -encoding UTF-8 -charset UTF-8 -d" +
                outputDir +
                filePath;

        // 使用ProcessBuilder拆分命令，以便正确处理参数
        Result result = getResult(projectDir, winMavenCommand);
        String line;
        // 循环读取并打印进程输出
        while ((line = result.reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待进程完成并获取退出码
        int exitCode = result.process.waitFor();
        // 打印进程退出码
        System.out.println("命令执行结束，退出码：" + exitCode);
    }

    /**
     * 执行Maven命令并获取结果
     *
     * @param projectDir      项目目录路径
     * @param winMavenCommand Maven命令
     * @return 执行结果封装在Result对象中
     * @throws IOException 如果启动进程时发生IO异常
     */
    private Result getResult(String projectDir, String winMavenCommand) throws IOException {
        // 初始化ProcessBuilder对象，并设置要执行的Maven命令
        ProcessBuilder processBuilder = new ProcessBuilder(winMavenCommand.split(" "));
        // 设置进程工作目录为项目目录
        processBuilder.directory(new File(projectDir));

        // 启动进程
        Process process = processBuilder.start();

        // 读取进程输出
        InputStream inputStream = process.getInputStream();
        // 使用BufferedReader读取进程输出流
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        // 返回进程和其输出的封装对象Result
        return new Result(process, reader);
    }
}
