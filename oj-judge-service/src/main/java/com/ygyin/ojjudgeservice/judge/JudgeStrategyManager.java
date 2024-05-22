package com.ygyin.ojjudgeservice.judge;

import com.ygyin.ojjudgeservice.judge.strategy.Context;
import com.ygyin.ojjudgeservice.judge.strategy.DefaultStrategyImpl;
import com.ygyin.ojjudgeservice.judge.strategy.JavaStrategyImpl;
import com.ygyin.ojjudgeservice.judge.strategy.JudgeStrategy;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.sandbox.TestInfo;
import org.springframework.stereotype.Service;

/**
 * 判题的策略管理
 */
@Service
public class JudgeStrategyManager {
    /**
     * 判题
     *
     * @param context
     * @return
     */
    TestInfo doProblemJudge(Context context) {
        // 根据提交题目的编程语言来决定策略
        ProblemSubmit problemSubmit = context.getProblemSubmit();
        String language = problemSubmit.getLanguage();
        // 默认为 default strategy
        JudgeStrategy strategy = new DefaultStrategyImpl();
        // 如果编程语言为 java 更换为 java 策略
        if ("java".equals(language))
            strategy = new JavaStrategyImpl();

        return strategy.doProblemJudge(context);
    }
}
