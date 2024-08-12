package com.joysuch.publish.getter;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.SymbolicRef;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.RemoteRefUpdate.Status;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Git工具类
 *
 * @author lizhe
 */
@Slf4j
public class GitUtils {

    /**
     * 提交文件到仓库 (包括新增的、更改的、删除的)
     *
     * @param git           Git操作对象，用于执行Git命令
     * @param credential    凭证提供者，用于提供访问仓库的凭据
     * @param filePattern   文件模式，用于指定需要添加到仓库的文件
     * @param commitMessage 提交信息，用于描述本次提交的变更
     * @return 返回操作是否成功
     * @throws GitAPIException 当Git操作异常时抛出
     * @throws IOException     当IO操作异常时抛出
     */
    public static boolean commitFiles(Git git, CredentialsProvider credential, String filePattern, String commitMessage) throws GitAPIException, IOException {
        // 添加指定模式的文件到仓库
        git.add().addFilepattern(filePattern).call();
        // 更新已添加的文件状态
        git.add().addFilepattern(filePattern).setUpdate(true).call();
        // 提交文件并设置提交信息
        git.commit().setMessage(commitMessage).call();
        // 推送提交到远程仓库
        Iterable<PushResult> pushResults = git.push().setCredentialsProvider(credential).call();
        // 打印提交和推送结果
        printCommitPushResult(pushResults, git.getRepository().getBranch(), commitMessage);
        return true;
    }


    /**
     * 打印提交文件的日志
     *
     * @param results       推送结果的集合
     * @param branchName    分支名称
     * @param commitMessage 提交信息
     */
    private static void printCommitPushResult(Iterable<PushResult> results, String branchName, String commitMessage) {
        // 记录git提交命令日志
        log.info("git add && git commit -m '{}'", commitMessage);
        // 记录git推送命令日志
        log.info("git push");
        // 遍历推送结果
        for (PushResult result : results) {
            // 获取远程分支更新信息
            RemoteRefUpdate remoteRefUpdate = result.getRemoteUpdate(GitBranchType.LOCAL.getPrefix() + branchName);
            // 根据推送状态记录日志
            if (Status.OK.equals(remoteRefUpdate.getStatus())) {
                // 推送成功时，打印远程日志信息
                log.info("remote: " + result.getMessages().substring(0, result.getMessages().length() - 1));
            } else {
                // 推送失败时，打印错误日志信息
                log.error("remote: " + result.getMessages());
            }
            // 打印推送的远程仓库URI
            log.info("To {}", result.getURI());
        }
    }


    /**
     * 删除分支
     *
     * @param git        Git操作对象
     * @param credential 凭证提供者，用于Git操作
     * @param branchName 需要删除的分支名称
     * @return 删除操作是否成功
     * @throws GitAPIException 抛出当Git操作异常
     * @throws IOException     抛出当IO操作异常
     */
    public static boolean removeBranch(Git git, CredentialsProvider credential, String branchName) throws GitAPIException, IOException {
        // master分支不能删除
        if ("master".equals(branchName)) {
            return false;
        }

        // 获取当前分支名称
        String oldBranch = git.getRepository().getBranch();
        // 如果要删除的分支等于当前分支，切换到master
        if (oldBranch.equals(branchName)) {
            git.checkout().setName("master").call();
        }
        // 删除本地分支
        git.branchDelete().setBranchNames(GitBranchType.LOCAL.getPrefix() + branchName).setForce(true).call();
        // 删除远程分支
        git.branchDelete().setBranchNames(GitBranchType.REMOTE.getPrefix() + branchName).setForce(true).call();
        // 推送到远程
        String branchFullName = GitBranchType.LOCAL.getPrefix() + branchName;
        RefSpec refSpec = new RefSpec(":" + branchFullName).setForceUpdate(true);
        Iterable<PushResult> results = git.push().setCredentialsProvider(credential).setRefSpecs(refSpec).call();
        // 打印删除分支结果
        printRemoveBranchResult(results, branchFullName, branchName);
        return true;
    }


    /**
     * 打印删除分支的日志
     *
     * @param results        删除分支操作的结果集合
     * @param branchFullName 分支的完整名称，用于在结果集中查找对应的更新
     * @param branchName     被删除的分支名称，用于日志输出
     */
    private static void printRemoveBranchResult(Iterable<PushResult> results, String branchFullName, String branchName) {
        // 输出执行删除分支的git命令
        log.info("git push origin --delete {}", branchName);
        // 遍历删除操作的结果集合
        for (PushResult result : results) {
            // 获取指定分支的远程引用更新信息
            RemoteRefUpdate remoteRefUpdate = result.getRemoteUpdate(branchFullName);
            // 检查远程引用更新的状态
            if (Status.OK.equals(remoteRefUpdate.getStatus())) {
                // 如果更新成功，打印成功消息
                log.info("remote: " + result.getMessages().substring(0, result.getMessages().length() - 1));
            } else {
                // 如果更新失败，打印错误消息
                log.error("remote: " + result.getMessages());
            }
            // 打印操作所指向的远程仓库URI
            log.info("To {}", result.getURI());
            // 打印删除分支的操作
            log.info("- [deleted]    {}", branchName);
        }
    }


    /**
     * 创建分支
     * <p>
     * 此方法用于在Git仓库中创建一个新的分支。它首先检查本地和远程仓库是否已经存在指定的分支。
     * 如果分支已经存在，则不会重复创建。如果指定的分支只存在于远程仓库，则会创建一个对应的本地分支。
     * 创建新分支后，会将其推送到远程仓库。
     *
     * @param git        Git操作对象，用于执行Git命令
     * @param credential 凭证提供者，用于提供访问远程仓库的凭据
     * @param branchName 要创建的分支名称
     * @return 创建的分支名称
     * @throws GitAPIException 如果Git操作失败，将抛出此异常
     * @throws IOException     如果读取分支信息失败，将抛出此异常
     */
    public static String createBranch(Git git, CredentialsProvider credential, String branchName) throws GitAPIException, IOException {
        //如果本地存在分支,直接返回
        if (getBranches(git, GitBranchType.LOCAL).contains(branchName)) {
            return branchName;
        }
        //如果远端存在分支，则创建本地分支
        if (getBranches(git, GitBranchType.REMOTE).contains(branchName)) {
            String oldBranch = git.getRepository().getBranch();
            git.checkout().setName(branchName).setCreateBranch(true).call();
            git.checkout().setName(oldBranch).call();
            return branchName;
        }
        //新建分支
        git.branchCreate().setName(branchName).call();
        String oldBranch = git.getRepository().getBranch();
        git.checkout().setName(branchName).call();
        //推送到远程
        git.push().setCredentialsProvider(credential).call();
        git.checkout().setName(oldBranch).call();
        return branchName;
    }


    /**
     * 获取所有分支
     *
     * @param git        Git操作对象
     * @param branchType 分支类型，分为本地分支和远程分支
     * @return 分支名称列表
     * @throws GitAPIException 当Git操作异常时抛出
     * @throws IOException     当IO操作异常时抛出
     */
    public static List<String> getBranches(Git git, GitBranchType branchType) throws GitAPIException, IOException {
        // 当branchType为本地分支时，获取所有本地分支名称
        if (GitBranchType.LOCAL.equals(branchType)) {
            List<Ref> refs = git.branchList().call();
            return refs.stream().map(ref -> ref.getName().substring(GitBranchType.LOCAL.getPrefix().length()))
                    .collect(Collectors.toList());
        } else {
            // 当branchType为远程分支时，获取所有远程分支名称
            List<Ref> refs = git.getRepository().getRefDatabase().getRefs();
            return refs.stream().filter(item -> !(item instanceof SymbolicRef))
                    .filter(item -> item.getName().startsWith(GitBranchType.REMOTE.getPrefix()))
                    .map(ref -> ref.getName().substring(GitBranchType.REMOTE.getPrefix().length()))
                    .collect(Collectors.toList());
        }
    }


    /**
     * 获取git对象
     * <p>
     * 此方法用于建立与git仓库的连接，如果本地路径已存在，则直接打开现有仓库，
     * 否则，使用提供的git URL克隆仓库到指定的本地路径。
     *
     * @param gitUrl              git的http路径，用于克隆仓库时指定源地址
     * @param credentialsProvider 认证信息提供者，用于验证git仓库访问权限
     * @param localPath           本地路径，指定克隆的仓库或将打开的现有仓库的路径
     * @return Git对象，代表与git仓库的连接
     * @throws IOException     如果本地路径不存在且无法创建，或者git操作失败时抛出
     * @throws GitAPIException 如果克隆或打开仓库过程中出现git API错误时抛出
     */
    public static Git getGit(String gitUrl, CredentialsProvider credentialsProvider, String localPath) throws IOException, GitAPIException {
        if (new File(localPath).exists()) {
            // 如果本地路径存在，则直接打开现有仓库
            return Git.open(new File(localPath));
        } else {
            // 如果本地路径不存在，则从git URL克隆仓库到指定的本地路径
            return Git.cloneRepository().setCredentialsProvider(credentialsProvider).setURI(gitUrl)
                    .setDirectory(new File(localPath)).call();
        }
    }


    /**
     * 关闭git
     *
     * @param git 需要关闭的Git实例
     */
    public static void closeGit(Git git) {
        git.close();
    }


    /**
     * 创建Git认证信息
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回一个CredentialsProvider对象，用于存储和提供认证信息
     */
    public static CredentialsProvider createCredentialsProvider(String username, String password) {
        return new UsernamePasswordCredentialsProvider(username, password);
    }


}
