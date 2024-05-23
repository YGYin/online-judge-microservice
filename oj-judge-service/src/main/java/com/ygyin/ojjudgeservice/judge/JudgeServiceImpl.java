package com.ygyin.ojjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.ygyin.ojcommon.common.ErrorCode;
import com.ygyin.ojcommon.exception.BusinessException;
import com.ygyin.ojjudgeservice.judge.sandbox.Sandbox;
import com.ygyin.ojjudgeservice.judge.sandbox.SandboxFactory;
import com.ygyin.ojjudgeservice.judge.sandbox.SandboxProxy;
import com.ygyin.ojjudgeservice.judge.strategy.Context;
import com.ygyin.ojmodel.model.dto.problem.TestCase;
import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.enums.ProblemSubmitStatusEnum;
import com.ygyin.ojmodel.model.sandbox.RunCodeRequest;
import com.ygyin.ojmodel.model.sandbox.RunCodeResponse;
import com.ygyin.ojmodel.model.sandbox.TestInfo;
import com.ygyin.ojservicecli.service.ProblemServiceFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    /**
     * 题目服务，用于查询题目信息和查询题目提交信息
     */
    @Resource
    private ProblemServiceFeignClient problemServiceFeignClient;

    @Resource
    private JudgeStrategyManager judgeStrategyManager;

    /**
     * 在 Spring 的 bean 中通过 @Value 读取配置文件中 sandbox.type 参数
     */
    @Value("${sandbox.type:sample}")
    private String type;

    @Override
    public ProblemSubmit doJudgeProblem(long problemSubmitId) {
        // 判题服务业务流程:
        // 1. 传入提交题目 id，获取对应的提交题目和提交信息 (代码，语言等)

        ProblemSubmit problemSubmit = problemServiceFeignClient.getProblemSubmitById(problemSubmitId);
        if (problemSubmit == null)
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交的题目信息不存在");

        // 根据提交题目获取题目 id，进而获取题目
        Long problemId = problemSubmit.getProblemId();
        Problem problem = problemServiceFeignClient.getProblemById(problemId);
        if (problem == null)
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在");

        // 2. 如果题目提交状态不为等待中，证明可能正在判题，无需再重复执行判题
        if (!problemSubmit.getTestStatus().equals(ProblemSubmitStatusEnum.WAITING_FOR_JUDGE.getValue()))
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已正在判题");

        // 3. 若可以判题，更改题目提交状态，即判题状态为正在判题，防止重复执行
        ProblemSubmit submitUpdate = new ProblemSubmit();
        submitUpdate.setId(problemSubmitId);
        submitUpdate.setTestStatus(ProblemSubmitStatusEnum.JUDGING.getValue());
        // 相当于上锁
        boolean isUpdate = problemServiceFeignClient.updateProblemSubmitById(submitUpdate);
        if (!isUpdate)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交状态失败");

        // 4. 调用代码沙箱，获取代码的运行结果
        Sandbox sandbox = SandboxFactory.newInstance(type);
        sandbox = new SandboxProxy(sandbox);

        String language = problemSubmit.getLanguage();
        String code = problemSubmit.getCode();
        // 从题目的 testcase 中获取输入列表
        String testCaseStr = problem.getTestCase();
        // 将字符串转换为 json 数组 (列表), TestCase 里面包含输入和输出列表
        List<TestCase> testCaseList = JSONUtil.toList(testCaseStr, TestCase.class);
        // 获取每一项 testCase 的 input，并 collect 成一个新的列表
        List<String> inputList = testCaseList.stream().map(TestCase::getInput).collect(Collectors.toList());

        // 基于 build，可以不需要通过 new，直接链式调用实现属性初始化并返回新对象
        RunCodeRequest runCodeRequest = RunCodeRequest.builder()
                .code(code)
                .inputList(inputList)
                .language(language)
                .build();

        RunCodeResponse runCodeResponse = sandbox.runCode(runCodeRequest);
        List<String> outputList = runCodeResponse.getOutputList();

        // 5. 根据代码沙箱返回的执行结果，再设置该题目的判题状态和信息
        // 使用了策略模式，将信息封装入上下文中，然后调用默认判题策略
        Context context = new Context();
        context.setTestInfo(runCodeResponse.getTestInfo());
        context.setProblem(problem);
        context.setProblemSubmit(problemSubmit);
        context.setInputList(inputList);
        context.setOutputList(outputList);
        context.setTestCaseList(testCaseList);
        // 需要根据不同需求调用不同策略，需要一个策略管理类来方便调用不同策略
//        JudgeStrategy defaultStrategy = new DefaultStrategyImpl();
        TestInfo testInfoResp = judgeStrategyManager.doProblemJudge(context);

        // 6. 需要修改数据库中的判题结果
        submitUpdate = new ProblemSubmit();
        submitUpdate.setId(problemSubmitId);
        submitUpdate.setTestStatus(ProblemSubmitStatusEnum.SUCCEED.getValue());
        submitUpdate.setTestInfo(JSONUtil.toJsonStr(testInfoResp));
        // 相当于上锁
        isUpdate = problemServiceFeignClient.updateProblemSubmitById(submitUpdate);
        if (!isUpdate)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交状态失败");

        //再从数据库中获取最新的题目提交状态
        ProblemSubmit problemSubmitRes = problemServiceFeignClient.getProblemSubmitById(problemId);

        return problemSubmitRes;
    }
}
