package com.joysuch.storage.cli;

import java.io.*;

public class GitInteractor {

    /**
     * 执行Git命令
     *
     * @param projectDir 项目目录
     * @param gitCommand Git命令
     * @throws IOException          如果创建或执行过程中发生I/O错误
     * @throws InterruptedException 如果当前线程被中断
     */
    public static void doGitAction(String projectDir, String gitCommand) throws IOException, InterruptedException {
        // 构建Windows系统下的Git命令

        // 创建进程构建器并设置命令
        ProcessBuilder processBuilder = new ProcessBuilder(gitCommand.split(" "));
        // 设置进程工作目录为指定的项目目录
        processBuilder.directory(new File(projectDir));

        // 启动进程
        Process process = processBuilder.start();

        // 获取进程的输出流
        InputStream inputStream = process.getInputStream();
        // 包装输入流以便按行读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        // 根据不同的Git命令输出不同的信息
        if ("git branch".equals(gitCommand)) {
            // 当git命令为branch时，读取并输出除第一行外的所有行
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("*")) {
                    System.out.println(line.trim());
                }
            }
        } else {
            // 对于其他git命令，读取并输出所有输出行
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // 等待进程执行完成并获取退出码
        int exitCode = process.waitFor();
        // 打印进程退出码
        System.out.println("命令执行结束，退出码：" + exitCode);
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        doGitAction("D:\\work-backend\\tiji-master-data", "branch");
    }
}