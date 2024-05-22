package com.ygyin.ojjudgeservice.judge.strategy;

import com.ygyin.ojmodel.model.dto.problem.TestCase;
import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.sandbox.TestInfo;
import lombok.Data;

import java.util.List;

/**
 * 上下文，定义在策略中传递的参数
 */

@Data
public class Context {

    private TestInfo testInfo;
    private Problem problem;

    private List<String> inputList;
    private List<String> outputList;
    private List<TestCase> testCaseList;
    private ProblemSubmit problemSubmit;

}
