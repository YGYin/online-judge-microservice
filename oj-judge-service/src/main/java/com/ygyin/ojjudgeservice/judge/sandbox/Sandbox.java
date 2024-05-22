package com.ygyin.ojjudgeservice.judge.sandbox;


import com.ygyin.ojmodel.model.sandbox.RunCodeRequest;
import com.ygyin.ojmodel.model.sandbox.RunCodeResponse;

/**
 * 代码沙箱接口
 */
public interface Sandbox {
    // 定义接口
    // 项目只调用接口，不调用具体实现类，在使用其他代码沙箱的实现类时无需修改名称，便于拓展

    RunCodeResponse runCode(RunCodeRequest runCodeRequest);
}
