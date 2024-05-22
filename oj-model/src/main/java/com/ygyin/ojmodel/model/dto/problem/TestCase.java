package com.ygyin.ojmodel.model.dto.problem;

import lombok.Data;

/**
 * 题目测试用例
 * Data 注解同于让 Lombok 自动生成 get / set 方法
 */
@Data
public class TestCase {
    /**
     * 输入用例
     */
    private String input;
    /**
     * 输出用例
     */
    private String output;
}
