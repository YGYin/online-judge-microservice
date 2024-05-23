package com.ygyin.ojservicecli.service;


import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 判题服务业务流程:
 * 1. 传入提交题目 id，获取对应的题目和提交信息 (代码，语言等)
 * 2. 调用代码沙箱，获取代码的运行结果
 * 3. 根据代码沙箱返回的执行结果，再设置该题目的判题状态和信息
 */

@FeignClient(name = "oj-judge-service", path = "/api/judge/inner")
public interface JudgeServiceFeignClient {

    /**
     * 进行判题
     * @param problemSubmitId
     * @return
     */
    @PostMapping("/do_judge")
    ProblemSubmit doJudgeProblem(@RequestParam("problemSubmitId") long problemSubmitId);

}
