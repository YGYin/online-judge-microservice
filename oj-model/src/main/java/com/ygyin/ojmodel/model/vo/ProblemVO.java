package com.ygyin.ojmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ygyin.ojmodel.model.dto.problem.TestConfig;
import com.ygyin.ojmodel.model.entity.Problem;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类，用于返回给前端对象，节约空间或者过滤字段
 *
 * @TableName problem
 */
@Data
public class ProblemVO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * tags 在实体类中为了要存到数据库中，是一个 String json
     * 在 VO 类中方便返回给前端为 List
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 判题配置(json object)
     */
    private TestConfig testConfig;

    /**
     * 提交数
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
     * 有可能会根据 userId 查询，然后查询题目创建人的具体信息
     * 题目创建人的信息，用于返回给前端
     */
    private UserVO userVO;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * VO 包装类转对象
     *
     * @param problemVO
     * @return
     */
    public static Problem voToObj(ProblemVO problemVO) {
        if (problemVO == null) {
            return null;
        }
        Problem problem = new Problem();
        // 能直接 copy 的字段属性直接 copy
        BeanUtils.copyProperties(problemVO, problem);
        List<String> voTags = problemVO.getTags();
        // 将 VO 对象的 List 转换为 String 存到 DB 中
        if (voTags != null)
            problem.setTags(JSONUtil.toJsonStr(voTags));

        // 同理，转换 TestConfig 为 String
        TestConfig voTestConfig = problemVO.testConfig;
        if (voTestConfig != null)
            problem.setTestConfig(JSONUtil.toJsonStr(voTestConfig));
        return problem;
    }

    /**
     * 对象转 VO 包装类
     *
     * @param problem
     * @return
     */
    public static ProblemVO objToVo(Problem problem) {
        if (problem == null) {
            return null;
        }
        ProblemVO problemVO = new ProblemVO();
        BeanUtils.copyProperties(problem, problemVO);
        // 将 DB 中的 tags 转换为 List 存入到 VO 对象中
        List<String> voTags = JSONUtil.toList(problem.getTags(), String.class);
        problemVO.setTags(voTags);

        TestConfig voTestConfig = JSONUtil.toBean(problem.getTestConfig(), TestConfig.class);
        problemVO.setTestConfig(voTestConfig);
        return problemVO;
    }
}