package com.ygyin.ojmodel.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题返回信息枚举
 *
 */
public enum TestInfoMsgEnum {

    ACCEPTED("判题成功", "accepted"),
    WRONG("答案错误", "wrong answer"),
    WAITING("等待中","waiting"),
    COMPILE_ERROR("编译错误", "compile error"),
    OVERTIME("超时", "out of time limit"),
    OUT_OF_MEMORY("内存溢出", "out of memory"),
    OUT_OF_OUTPUT_LIMIT("输出溢出", "out of output limit"),
    RUNTIME_ERROR("运行错误", "runtime error"),
    SYSTEM_ERROR("系统错误", "system error");
    private final String text;

    private final String value;

    TestInfoMsgEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static TestInfoMsgEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (TestInfoMsgEnum anEnum : TestInfoMsgEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
