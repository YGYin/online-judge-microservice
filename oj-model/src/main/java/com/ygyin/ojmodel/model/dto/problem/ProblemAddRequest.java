package com.ygyin.ojmodel.model.dto.problem;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 */
@Data
public class ProblemAddRequest implements Serializable {
    /*
        创建题目时所需字段
     */

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题解
     */
    private String res;

    /**
     * 标签列表（json 数组）
     * 用 List 来接收，将 List 转换成 json 再输入给后端，方便前端处理
     */
    private List<String> tags;

    /**
     * 测试用例(json array)
     */
    private List<TestCase> testCase;

    /**
     * 判题配置(json object)
     */
    private TestConfig testConfig;

    private static final long serialVersionUID = 1L;
}