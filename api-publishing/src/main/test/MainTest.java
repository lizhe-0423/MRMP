import com.joysuch.publish.ApiPublishingApplication;

import com.joysuch.publish.getter.GitBranchType;
import com.joysuch.publish.getter.GitUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(classes = ApiPublishingApplication.class) // 指定启动类
public class MainTest {


    /**
     * 测试Git操作的测试方法
     *
     * @throws GitAPIException 如果Git API操作失败
     * @throws IOException     如果IO操作失败
     */
    @Test
    public void test() throws GitAPIException, IOException {
        // 定义Git仓库的URL
        String gitUrl = "https://gitee.com/smart-it-work/smart-stream.git";
        // 用户名
        String username = "imchentiefeng@aliyun.com";
        // 密码
        String password = "********";
        // 本地目录
        String localDir = "D:\\test\\smart-stream";

        // 创建凭据提供程序
        CredentialsProvider credential = GitUtils.createCredentialsProvider(username, password);
        // 获取Git实例
        Git git = GitUtils.getGit(gitUrl, credential, localDir);

        // 获取本地分支列表
        List<String> localBranches = GitUtils.getBranches(git, GitBranchType.LOCAL);
        // 获取远程分支列表
        List<String> remoteBranches = GitUtils.getBranches(git, GitBranchType.REMOTE);

        // 创建名为"test"的分支
        String test = GitUtils.createBranch(git, credential, "test");
        // 删除名为"test"的分支
        boolean flag = GitUtils.removeBranch(git, credential, "test");
        // 提交更改
        boolean commitFlag = GitUtils.commitFiles(git, credential, ".", "test");

        System.out.println("本地分支列表：" + localBranches);
        System.out.println("远程分支列表：" + remoteBranches);
        System.out.println("创建分支：" + test);
        System.out.println("删除分支：" + flag);
        System.out.println("提交更改：" + commitFlag);
        // 关闭Git实例
        GitUtils.closeGit(git);
    }


}
