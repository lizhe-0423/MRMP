package com.joysuch.publish.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;

@Command(name = "GitCLI", version = "GitCLI 1.0", mixinStandardHelpOptions = true)
public class GitCLI implements Runnable {

    @Option(names = { "-p", "--project-dir" }, description = "Project directory")
    String projectDir;

    @Option(names = { "-c", "--git-command" }, description = "Git command to execute")
    String gitCommand;

    @Override
    public void run() {
        try {
            com.joysuch.publish.cli.GitInteractor.doGitAction(projectDir, gitCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GitCLI()).execute(args);
        System.exit(exitCode);
    }

}
