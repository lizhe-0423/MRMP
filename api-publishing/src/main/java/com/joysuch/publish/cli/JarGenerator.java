package com.joysuch.storage.cli;

import org.jetbrains.annotations.NotNull;

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
        while ((line = result.reader().readLine()) != null) {
            System.out.println(line);
        }

        // 等待进程完成并获取退出码
        int exitCode = result.process().waitFor();
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
    private static @NotNull Result getResult(String projectDir, String winMavenCommand) throws IOException {
        // 初始化ProcessBuilder对象，并设置要执行的Maven命令
        ProcessBuilder processBuilder = getProcessBuilder(winMavenCommand);
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
     * 构建并返回一个用于执行Maven命令的ProcessBuilder对象
     *
     * @param winMavenCommand 需要执行的Maven命令，带参数
     * @return ProcessBuilder对象，用于启动新的系统进程来执行Maven命令
     */
    private static @NotNull ProcessBuilder getProcessBuilder(String winMavenCommand) {
        // 使用ProcessBuilder启动新的系统进程来执行Maven命令
        // 将命令字符串分割成数组，作为ProcessBuilder的参数
        return new ProcessBuilder(winMavenCommand.split(" "));
    }


    /**
     * 表示执行命令后的结果记录类
     *
     * @param process 执行命令后得到的进程对象
     * @param reader  用于读取进程输出的缓冲读取器
     */
    private record Result(Process process, BufferedReader reader) {
    }


}
