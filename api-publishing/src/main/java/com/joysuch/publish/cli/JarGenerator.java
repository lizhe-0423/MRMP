package com.joysuch.publish.cli;

import java.io.*;

public class JarGenerator {

    /**
     * 执行代码生成过程
     * 此方法首先清理并打包项目，然后执行生成过程
     *
     * @param projectDir 项目目录路径
     * @throws IOException          当创建进程或读取输出时发生I/O错误
     * @throws InterruptedException 当等待进程完成时线程被中断
     */
    public static void doGenerate(String projectDir) throws IOException, InterruptedException {

        // 定义Windows环境下的Maven命令，用于清理并打包项目，跳过测试
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
//        String otherMavenCommand = "mvn clean package -DskipTests=true"

        // 使用ProcessBuilder拆分命令，以便正确处理参数
        Result result = getResult(projectDir, winMavenCommand);
        String line;
        // 循环读取并打印进程输出
        while ((line = result.reader.readLine())!= null) {
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
    private static Result getResult(String projectDir, String winMavenCommand) throws IOException {
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

    /**
     * 表示执行命令后的结果记录类
     */
    private static class Result {
        private Process process;
        private BufferedReader reader;

        public Result(Process process, BufferedReader reader) {
            this.process = process;
            this.reader = reader;
        }
    }
}