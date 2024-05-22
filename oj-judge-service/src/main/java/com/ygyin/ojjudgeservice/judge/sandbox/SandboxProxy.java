package com.ygyin.ojjudgeservice.judge.sandbox;

import com.ygyin.ojmodel.model.sandbox.RunCodeRequest;
import com.ygyin.ojmodel.model.sandbox.RunCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SandboxProxy implements Sandbox{

    /**
     * 引入实例(只会被改变一次)，用于调用原来的沙箱内的方法
     */
    private final Sandbox sandbox;

    public SandboxProxy(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    /**
     * 通过代理类来调用原本沙箱实现类的功能，并在此实现额外功能例如打印日志
     * 可以不用改变原本代码实现类，对于调用者无需再再每个调用沙箱的地方写日志代码
     * @param runCodeRequest
     * @return
     */
    @Override
    public RunCodeResponse runCode(RunCodeRequest runCodeRequest) {
        log.info("代码沙箱请求信息: "+runCodeRequest.toString() );
        RunCodeResponse runCodeResponse = sandbox.runCode(runCodeRequest);
        log.info("代码沙箱响应信息: "+runCodeResponse.toString());
        return runCodeResponse;
    }
}
