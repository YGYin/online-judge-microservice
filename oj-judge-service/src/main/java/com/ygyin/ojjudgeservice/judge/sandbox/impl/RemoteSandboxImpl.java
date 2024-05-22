package com.ygyin.ojjudgeservice.judge.sandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ygyin.ojcommon.common.ErrorCode;
import com.ygyin.ojcommon.exception.BusinessException;
import com.ygyin.ojjudgeservice.judge.sandbox.Sandbox;
import com.ygyin.ojmodel.model.sandbox.RunCodeRequest;
import com.ygyin.ojmodel.model.sandbox.RunCodeResponse;


/**
 * 用于实际调用业务流程的代码沙箱
 */
public class RemoteSandboxImpl implements Sandbox {
    private static final String AUTH_REQ_SECRET_KEY = "authKey";
    private static final String AUTH_REQ_HEADER = "authHeader";
    @Override
    public RunCodeResponse runCode(RunCodeRequest runCodeRequest) {
        System.out.println("实际远程沙箱");
        String url = "http://localhost:8080/runCode";
        String reqJsonStr = JSONUtil.toJsonStr(runCodeRequest);
        String respStr = HttpUtil.createPost(url).
                header(AUTH_REQ_HEADER,AUTH_REQ_SECRET_KEY)
                .body(reqJsonStr)
                .execute()
                .body();

        if (StringUtils.isBlank(respStr))
            throw new BusinessException(ErrorCode.API_ERROR, "调用远程沙箱 API 错误: " + respStr);

        return JSONUtil.toBean(respStr, RunCodeResponse.class);
    }
}
