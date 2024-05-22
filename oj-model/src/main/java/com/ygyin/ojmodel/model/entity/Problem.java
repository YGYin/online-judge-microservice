package com.ygyin.ojmodel.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目
 * @TableName problem
 */
@TableName(value ="problem")
@Data
public class Problem implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * tags 在实体类中为了要存到数据库中，是一个 String json
     * 在 VO 类中方便返回给前端为 List
     * 标签列表（json 数组）
     */
    private String tags;

    /**
     * 测试用例(json array)
     */
    private String testCase;

    /**
     * 判题配置(json object)
     */
    private String testConfig;

    /**
     * 点赞数
     */
    private Integer submitNum;

    /**
     * 收藏数
     */
    private Integer favorNum;

    /**
     * 收藏数
     */
    private Integer acNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}