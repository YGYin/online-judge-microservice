package com.ygyin.ojmodel.model.dto.problemsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求，考虑用户提交一个题目需要哪些参数
 *
 */
@Data
public class ProblemSubmitAddRequest implements Serializable {

    /**
     * 语言
     */
    private String language;

    /**
     * 用户提交代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long problemId;

    // 创建用户 id 不用提交，从后台的 session 当前登录用户的状态中去取

    private static final long serialVersionUID = 1L;
}