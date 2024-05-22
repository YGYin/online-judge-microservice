package com.ygyin.ojmodel.model.dto.problemsubmit;

import com.ygyin.ojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询提交题目的请求，考虑用户提交一个题目需要哪些参数
 * 为了是查询字段有排序规则和排序状态，继承 PageRequest
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProblemSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 语言
     */
    private String language;

    /**
     * 题目 id
     */
    private Long problemId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 提交状态，用包装类，前端有可能不传，没有必要给初始值
     */
    private Integer testStatus;

    // 创建用户 id 不用提交，从后台的 session 当前登录用户的状态中去取

    private static final long serialVersionUID = 1L;
}