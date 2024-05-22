package com.ygyin.ojjudgeservice.judge.sandbox.impl;


import com.ygyin.ojjudgeservice.judge.sandbox.Sandbox;
import com.ygyin.ojmodel.model.enums.ProblemSubmitStatusEnum;
import com.ygyin.ojmodel.model.enums.TestInfoMsgEnum;
import com.ygyin.ojmodel.model.sandbox.RunCodeRequest;
import com.ygyin.ojmodel.model.sandbox.RunCodeResponse;
import com.ygyin.ojmodel.model.sandbox.TestInfo;

import java.util.List;

/**
 * 用于接入第三方现成的代码沙箱，调用现成的第三方代码沙箱
 */
public class ExternalSandboxImpl implements Sandbox {
    @Override
    public RunCodeResponse runCode(RunCodeRequest runCodeRequest) {
        List<String> inputList = runCodeRequest.getInputList();

        RunCodeResponse runCodeResponse = new RunCodeResponse();
        runCodeResponse.setOutputList(inputList);
        runCodeResponse.setRunStatus(ProblemSubmitStatusEnum.SUCCEED.getValue());
        runCodeResponse.setRunMsg("代码沙箱测试执行成功");

        TestInfo testInfo = new TestInfo();
        testInfo.setMsg(TestInfoMsgEnum.ACCEPTED.getText());
        testInfo.setMemory(1000L);
        testInfo.setTime(1000L);

        runCodeResponse.setTestInfo(testInfo);

        return runCodeResponse;
    }
}
