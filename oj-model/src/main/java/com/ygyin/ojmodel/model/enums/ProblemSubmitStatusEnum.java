package com.ygyin.ojmodel.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交状态枚举，用于校验用户的题目提交状态
 */
public enum ProblemSubmitStatusEnum {
    // 判题状态(0-waiting, 1-judging, 2-success, 3-fail)
    WAITING_FOR_JUDGE("等待判题中", 0),
    JUDGING("正在判题", 1),
    SUCCEED("判题成功", 2),
    FAILED("判题失败", 3);

    private final String text;

    private final Integer value;

    ProblemSubmitStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ProblemSubmitStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ProblemSubmitStatusEnum anEnum : ProblemSubmitStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
