package com.ygyin.ojproblemservice.controller.inner;

import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojproblemservice.service.ProblemService;
import com.ygyin.ojproblemservice.service.ProblemSubmitService;
import com.ygyin.ojservicecli.service.ProblemServiceFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 仅供内部服务间调用的 problem controller
 */
@RestController
@RequestMapping("/inner")
public class InnerProblemController implements ProblemServiceFeignClient {

    @Resource
    private ProblemService problemService;

    @Resource
    private ProblemSubmitService problemSubmitService;

    /**
     * 通过 id 获取 problem
     *
     * @param problemId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public Problem getProblemById(@RequestParam("problemId") long problemId) {
        return problemService.getById(problemId);
    }

    // todo: Maybe problem Id

    /**
     * 根据 problem 提交 id 获取提交的 problem
     *
     * @param problemSubmitId
     * @return
     */
    @Override
    @GetMapping("/problem_submit/get/id")
    public ProblemSubmit getProblemSubmitById(@RequestParam("problemSubmitId") long problemSubmitId) {
        return problemSubmitService.getById(problemSubmitId);
    }

    /**
     * 更新提交题目
     *
     * @param problemSubmit
     * @return
     */
    @Override
    @PostMapping("/problem_submit/update")
    public Boolean updateProblemSubmitById(@RequestBody ProblemSubmit problemSubmit) {
        return problemSubmitService.updateById(problemSubmit);
    }

}
