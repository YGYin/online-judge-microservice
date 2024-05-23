package com.ygyin.ojjudgeservice.controller.inner;

import com.ygyin.ojjudgeservice.judge.JudgeService;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojservicecli.service.JudgeServiceFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 仅供内部服务间调用的 problem controller
 */
@RestController("/inner")
public class InnerJudgeController implements JudgeServiceFeignClient {

    @Resource
    private JudgeService judgeService;

    /**
     * 进行判题
     *
     * @param problemSubmitId
     * @return
     */
    @Override
    @PostMapping("/do_judge")
    public ProblemSubmit doJudgeProblem(@RequestParam("problemSubmitId") long problemSubmitId) {
        return judgeService.doJudgeProblem(problemSubmitId);
    }
}
