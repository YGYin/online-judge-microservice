package com.ygyin.ojmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.sandbox.TestInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 提交题目封装类，用于返回给前端对象，节约空间或者过滤字段
 *
 * @TableName problem
 */
@Data
public class ProblemSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 语言
     */
    private String language;

    /**
     * 用户提交代码
     */
    private String code;

    /**
     * 判题状态(0-waiting 1-judging 2-success 3-fail)
     */
    private Integer testStatus;

    /**
     * 判题信息(json object)
     */
    private TestInfo testInfo;

    /**
     * 题目 id
     */
    private Long problemId;

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
     * 提交题目的用户信息
     */
    private UserVO userVO;

    /**
     * 对应题目信息
     */
    private ProblemVO problemVO;

    private static final long serialVersionUID = 1L;

    /**
     * VO 包装类转对象
     *
     * @param problemSubmitVO
     * @return
     */
    public static ProblemSubmit voToObj(ProblemSubmitVO problemSubmitVO) {
        if (problemSubmitVO == null) {
            return null;
        }
        ProblemSubmit problemSubmit = new ProblemSubmit();
        // 能直接 copy 的字段属性直接 copy
        BeanUtils.copyProperties(problemSubmitVO, problemSubmit);
        TestInfo voTestInfo = problemSubmitVO.getTestInfo();
        // 将 VO 对象的 List 转换为 String 存到 DB 中
        if (voTestInfo != null)
            problemSubmit.setTestInfo(JSONUtil.toJsonStr(voTestInfo));

        return problemSubmit;
    }

    /**
     * 对象转 VO 包装类
     *
     * @param problemSubmit
     * @return
     */
    public static ProblemSubmitVO objToVo(ProblemSubmit problemSubmit) {
        if (problemSubmit == null) {
            return null;
        }
        ProblemSubmitVO problemSubmitVO = new ProblemSubmitVO();
        BeanUtils.copyProperties(problemSubmit, problemSubmitVO);
        // 将 DB 中的 testInfo 转换为对应 Bean 存入到 VO 对象中
        TestInfo voTestInfo = JSONUtil.toBean(problemSubmit.getTestInfo(), TestInfo.class);
        problemSubmitVO.setTestInfo(voTestInfo);

        return problemSubmitVO;
    }
}