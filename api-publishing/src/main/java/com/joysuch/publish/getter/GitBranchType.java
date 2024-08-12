package com.joysuch.publish.getter;

import lombok.Getter;

/**
 * git分支类型
 *
 * @author lizhe@joysuch.com
 * @date 2024/08/12
 */
@Getter
public enum GitBranchType {
    /**
     * 本地分支
     */
    LOCAL("refs/heads/"),
    /**
     * 远程分支
     */
    REMOTE("refs/remotes/origin/");
    /**
     * 分支前缀
     */
    private final String prefix;
    GitBranchType(String prefix) {
        this.prefix = prefix;
    }
}
