package com.ygyin.ojservicecli.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ygyin.ojmodel.model.dto.problem.ProblemQueryRequest;
import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.vo.ProblemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
* @author yg
* @description 针对表【problem(题目)】的数据库操作Service
* @createDate 2024-03-30 18:11:42
*/
@FeignClient(name = "oj-problem-service", path = "/api/problem/inner")
public interface ProblemServiceFeignClient {

    /**
     * 通过 id 获取 problem
     * @param problemId
     * @return
     */
    @GetMapping("/get/id")
    Problem getProblemById(@RequestParam("problemId") long problemId);

    // todo: Maybe problem Id

    /**
     * 根据 problem 提交 id 获取提交的 problem
     * @param problemSubmitId
     * @return
     */
    @GetMapping("/problem_submit/get/id")
    ProblemSubmit getProblemSubmitById(@RequestParam("problemSubmitId") long problemSubmitId);

    /**
     * 更新提交题目
     * @param problemSubmit
     * @return
     */
    @PostMapping("/problem_submit/update")
    Boolean updateProblemSubmitById(@RequestBody ProblemSubmit problemSubmit);

}
