package com.ygyin.ojjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.ygyin.ojmodel.model.dto.problem.TestCase;
import com.ygyin.ojmodel.model.dto.problem.TestConfig;
import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.enums.TestInfoMsgEnum;
import com.ygyin.ojmodel.model.sandbox.TestInfo;

import java.util.List;
import java.util.Optional;

/**
 * 默认策略
 */
public class JavaStrategyImpl implements JudgeStrategy {
    /**
     * 使用默认策略实现判题
     *
     * @param context
     * @return
     */
    @Override
    public TestInfo doProblemJudge(Context context) {

        TestInfo testInfo = context.getTestInfo();
        Long memory = Optional.ofNullable(testInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(testInfo.getTime()).orElse(0L);
        Problem problem = context.getProblem();
        List<String> inputList = context.getInputList();
        List<String> outputList = context.getOutputList();
        List<TestCase> testCaseList = context.getTestCaseList();

        // testInfoResp 中的默认的 msg 为 AC
        TestInfoMsgEnum testInfoMsgEnum = TestInfoMsgEnum.ACCEPTED;

        TestInfo testInfoResp = new TestInfo();
        testInfoResp.setMemory(memory);
        testInfoResp.setTime(time);

        // 5.1 先判断沙箱执行结果的输出数量和预期输出数量是否相等
        if (outputList.size() != inputList.size()) {
            testInfoMsgEnum = TestInfoMsgEnum.WRONG;
            testInfoResp.setMsg(testInfoMsgEnum.getValue());
            return testInfoResp;
        }
        // 5.2 再依次判断每一项输出和预期输出相等
        for (int i = 0; i < testCaseList.size(); i++) {
            TestCase testCase = testCaseList.get(i);
            // 如果 outputList 与 testCase 中的 output 不相等则答案错误
            if (!testCase.getOutput().equals(outputList.get(i))) {
                testInfoMsgEnum = TestInfoMsgEnum.WRONG;
                testInfoResp.setMsg(testInfoMsgEnum.getValue());
                return testInfoResp;
            }
        }
        // 5.3 最后判断是否符合题目的限制要求

        // 获取 TestConfig 中对内存和运行时间的实际要求
        String testConfigStr = problem.getTestConfig();
        TestConfig testConfig = JSONUtil.toBean(testConfigStr, TestConfig.class);

        Long timeLimit = testConfig.getTimeLimit();
        Long memLimit = testConfig.getMemLimit();
        // Java 需要额外执行时间
        long JAVA_EXTRA_RUNNING_TIME = 1000L;
        if (time - JAVA_EXTRA_RUNNING_TIME > timeLimit) {
            testInfoMsgEnum = TestInfoMsgEnum.OVERTIME;
            testInfoResp.setMsg(testInfoMsgEnum.getValue());
            return testInfoResp;
        }
        if (memory > memLimit) {
            testInfoMsgEnum = TestInfoMsgEnum.OUT_OF_MEMORY;
            testInfoResp.setMsg(testInfoMsgEnum.getValue());
            return testInfoResp;
        }

        testInfoResp.setMsg(testInfoMsgEnum.getValue());
        return testInfoResp;
    }
}
