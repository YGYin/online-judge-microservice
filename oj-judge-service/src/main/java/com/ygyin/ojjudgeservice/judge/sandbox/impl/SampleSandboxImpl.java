package com.ygyin.ojjudgeservice.judge.sandbox.impl;

import com.ygyin.ojjudgeservice.judge.sandbox.Sandbox;
import com.ygyin.ojmodel.model.enums.ProblemSubmitStatusEnum;
import com.ygyin.ojmodel.model.enums.TestInfoMsgEnum;
import com.ygyin.ojmodel.model.sandbox.RunCodeRequest;
import com.ygyin.ojmodel.model.sandbox.RunCodeResponse;
import com.ygyin.ojmodel.model.sandbox.TestInfo;

import java.util.List;

/**
 * 用于示例测试业务流程的代码沙箱
 */
public class SampleSandboxImpl implements Sandbox {
    @Override
    public RunCodeResponse runCode(RunCodeRequest runCodeRequest) {
        List<String> inputList = runCodeRequest.getInputList();
        // New response and all set
        RunCodeResponse runCodeResponse = new RunCodeResponse();
        runCodeResponse.setOutputList(inputList);
        runCodeResponse.setRunStatus(ProblemSubmitStatusEnum.SUCCEED.getValue());
        runCodeResponse.setRunMsg("Sample Sandbox 执行成功");

        TestInfo testInfo = new TestInfo();
        testInfo.setMsg(TestInfoMsgEnum.ACCEPTED.getValue());
        testInfo.setMemory(1000L);
        testInfo.setTime(1000L);

        runCodeResponse.setTestInfo(testInfo);

        return runCodeResponse;
    }
}
