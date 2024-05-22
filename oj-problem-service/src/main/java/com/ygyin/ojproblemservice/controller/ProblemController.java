package com.ygyin.ojproblemservice.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ygyin.ojcommon.annotation.AuthCheck;
import com.ygyin.ojcommon.common.BaseResponse;
import com.ygyin.ojcommon.common.DeleteRequest;
import com.ygyin.ojcommon.common.ErrorCode;
import com.ygyin.ojcommon.common.ResultUtils;
import com.ygyin.ojcommon.constant.UserConstant;
import com.ygyin.ojcommon.exception.BusinessException;
import com.ygyin.ojcommon.exception.ThrowUtils;
import com.ygyin.ojmodel.model.dto.problem.*;
import com.ygyin.ojmodel.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.ygyin.ojmodel.model.dto.problemsubmit.ProblemSubmitQueryRequest;
import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.entity.User;
import com.ygyin.ojmodel.model.vo.ProblemSubmitVO;
import com.ygyin.ojmodel.model.vo.ProblemVO;
import com.ygyin.ojproblemservice.service.ProblemService;
import com.ygyin.ojproblemservice.service.ProblemSubmitService;
import com.ygyin.ojservicecli.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/problem")
@Slf4j
public class ProblemController {

    @Resource
    private ProblemService problemService;

    @Resource
    private UserService userService;

    @Resource
    private ProblemSubmitService problemSubmitService;


    // region 增删改查

    /**
     * 创建
     *
     * @param problemAddRequest 接受前端的请求对象
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addProblem(@RequestBody ProblemAddRequest problemAddRequest, HttpServletRequest request) {
        if (problemAddRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        Problem problem = new Problem();
        BeanUtils.copyProperties(problemAddRequest, problem);

        List<String> tags = problemAddRequest.getTags();
        if (tags != null)
            problem.setTags(JSONUtil.toJsonStr(tags));

        // 获取 TestCase 和 TestConfig
        List<TestCase> testCase = problemAddRequest.getTestCase();
        if (testCase != null)
            problem.setTestCase(JSONUtil.toJsonStr(testCase));
        TestConfig testConfig = problemAddRequest.getTestConfig();
        if (testConfig != null)
            problem.setTestConfig(JSONUtil.toJsonStr(testConfig));

        problemService.validProblem(problem, true);
        User loginUser = userService.getLoginUser(request);
        problem.setUserId(loginUser.getId());
        problem.setFavorNum(0);
        boolean result = problemService.save(problem);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newProblemId = problem.getId();
        return ResultUtils.success(newProblemId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteProblem(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Problem oldProblem = problemService.getById(id);
        ThrowUtils.throwIf(oldProblem == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldProblem.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = problemService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param problemUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateProblem(@RequestBody ProblemUpdateRequest problemUpdateRequest) {
        if (problemUpdateRequest == null || problemUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemUpdateRequest, problem);
        List<String> tags = problemUpdateRequest.getTags();
        if (tags != null) {
            problem.setTags(JSONUtil.toJsonStr(tags));
        }

        // 获取 TestCase 和 TestConfig
        List<TestCase> testCase = problemUpdateRequest.getTestCase();
        if (testCase != null)
            problem.setTestCase(JSONUtil.toJsonStr(testCase));
        TestConfig testConfig = problemUpdateRequest.getTestConfig();
        if (testConfig != null)
            problem.setTestConfig(JSONUtil.toJsonStr(testConfig));

        // 参数校验
        problemService.validProblem(problem, false);
        long id = problemUpdateRequest.getId();
        // 判断是否存在
        Problem oldProblem = problemService.getById(id);
        ThrowUtils.throwIf(oldProblem == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = problemService.updateById(problem);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Problem> getProblemById(long id, HttpServletRequest request) {
        if (id <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        Problem problem = problemService.getById(id);
        if (problem == null)
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);

        // 权限校验，先获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 如果查看题目的不是本人且不是管理人，抛出权限异常，不允许其获取全部信息
        if (!problem.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser))
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        return ResultUtils.success(problem);
    }

    /**
     * 根据 id 获取脱敏包装类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ProblemVO> getProblemVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Problem problem = problemService.getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(problemService.getProblemVO(problem, request));
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param problemQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Problem>> listProblemByPage(@RequestBody ProblemQueryRequest problemQueryRequest) {
        long current = problemQueryRequest.getCurrent();
        long size = problemQueryRequest.getPageSize();
        Page<Problem> problemPage = problemService.page(new Page<>(current, size),
                problemService.getQueryWrapper(problemQueryRequest));
        return ResultUtils.success(problemPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param problemQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ProblemVO>> listProblemVOByPage(@RequestBody ProblemQueryRequest problemQueryRequest,
                                                             HttpServletRequest request) {
        long current = problemQueryRequest.getCurrent();
        long size = problemQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Problem> problemPage = problemService.page(new Page<>(current, size),
                problemService.getQueryWrapper(problemQueryRequest));
        return ResultUtils.success(problemService.getProblemVOPage(problemPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param problemQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ProblemVO>> listMyProblemVOByPage(@RequestBody ProblemQueryRequest problemQueryRequest,
                                                               HttpServletRequest request) {
        if (problemQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        problemQueryRequest.setUserId(loginUser.getId());
        long current = problemQueryRequest.getCurrent();
        long size = problemQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Problem> problemPage = problemService.page(new Page<>(current, size),
                problemService.getQueryWrapper(problemQueryRequest));
        return ResultUtils.success(problemService.getProblemVOPage(problemPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param problemEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editProblem(@RequestBody ProblemEditRequest problemEditRequest, HttpServletRequest request) {
        if (problemEditRequest == null || problemEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemEditRequest, problem);
        List<String> tags = problemEditRequest.getTags();
        if (tags != null) {
            problem.setTags(JSONUtil.toJsonStr(tags));
        }

        // 获取 TestCase 和 TestConfig
        List<TestCase> testCase = problemEditRequest.getTestCase();
        if (testCase != null)
            problem.setTestCase(JSONUtil.toJsonStr(testCase));
        TestConfig testConfig = problemEditRequest.getTestConfig();
        if (testConfig != null)
            problem.setTestConfig(JSONUtil.toJsonStr(testConfig));

        // 参数校验
        problemService.validProblem(problem, false);
        User loginUser = userService.getLoginUser(request);
        long id = problemEditRequest.getId();
        // 判断是否存在
        Problem oldProblem = problemService.getById(id);
        ThrowUtils.throwIf(oldProblem == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldProblem.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = problemService.updateById(problem);
        return ResultUtils.success(result);
    }

    /**
     * 提交题目
     *
     * @param problemSubmitAddRequest 接收前端参数
     * @param request
     * @return 提交记录的 id
     */
    @PostMapping("/problem_submit/do")
    public BaseResponse<Long> doProblemSubmit(@RequestBody ProblemSubmitAddRequest problemSubmitAddRequest,
                                              HttpServletRequest request) {
        // 先判断请求参数不能为空，提交的题目 id 不能为空
        if (problemSubmitAddRequest == null || problemSubmitAddRequest.getProblemId() <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long problemSubmitId = problemSubmitService.doProblemSubmit(problemSubmitAddRequest, loginUser);
        return ResultUtils.success(problemSubmitId);
    }

    /**
     * 分页获取提交的题目列表（仅管理员可查看全部信息，普通用户仅能查看非答案即提交代码等公开信息）
     *
     * @param problemSubmitQueryRequest
     * @return
     */
    @PostMapping("/problem_submit/list/page")
    public BaseResponse<Page<ProblemSubmitVO>> listProblemSubmitByPage(@RequestBody ProblemSubmitQueryRequest problemSubmitQueryRequest, HttpServletRequest request) {
        // 先从用户请求中获取查询条件，然后直接调用 ProblemSubmitService
        long current = problemSubmitQueryRequest.getCurrent();
        long size = problemSubmitQueryRequest.getPageSize();
        // 调用的为 MyBatis Plus 所生成的分页方法，查出所有满足条件的原始的提交题目 page，
        Page<ProblemSubmit> problemSubmitPage = problemSubmitService.page(new Page<>(current, size),
                problemSubmitService.getQueryWrapper(problemSubmitQueryRequest));
        // 提前用 request 获取当前 loginUser 供后面使用
        final User loginUser = userService.getLoginUser(request);
        // 进行脱敏，通过 request 获取当前登录用户判断对应权限，再返回脱敏后的 VO Page，
        return ResultUtils.success(problemSubmitService.getProblemSubmitVOPage(problemSubmitPage, loginUser));
    }

}
