package com.ygyin.ojmodel.model.dto.problem;

import lombok.Data;

/**
 * 题目测试配置
 */
@Data
public class TestConfig {

    /**
     * 时间限制 / ms
     */
    private Long timeLimit;

    /**
     * 内存限制 / KB
     */
    private Long memLimit;

    /**
     * 堆栈限制 / KB
     */
    private Long stackLimit;
}
