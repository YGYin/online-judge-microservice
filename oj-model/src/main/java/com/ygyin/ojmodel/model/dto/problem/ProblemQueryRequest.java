package com.ygyin.ojmodel.model.dto.problem;

import com.ygyin.ojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProblemQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long id;

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
     */
    private List<String> tags;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}