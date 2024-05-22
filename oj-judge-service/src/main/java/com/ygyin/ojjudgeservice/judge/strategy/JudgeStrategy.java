package com.ygyin.ojjudgeservice.judge.strategy;


import com.ygyin.ojmodel.model.sandbox.TestInfo;

/**
 * 策略模式
 * 定义判题策略
 */
public interface JudgeStrategy {

    /**
     * 判题
     * @param context
     * @return
     */
    TestInfo doProblemJudge(Context context);
}
