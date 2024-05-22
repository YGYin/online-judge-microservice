package com.ygyin.ojjudgeservice.judge;


import com.ygyin.ojmodel.model.entity.ProblemSubmit;

/**
 * 判题服务业务流程:
 * 1. 传入提交题目 id，获取对应的题目和提交信息 (代码，语言等)
 * 2. 调用代码沙箱，获取代码的运行结果
 * 3. 根据代码沙箱返回的执行结果，再设置该题目的判题状态和信息
 */
public interface JudgeService {

    /**
     * 进行判题
     * @param problemSubmitId
     * @return
     */
    ProblemSubmit doJudgeProblem(long problemSubmitId);

}
