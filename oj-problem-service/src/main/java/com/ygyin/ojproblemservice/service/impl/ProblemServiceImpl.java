package com.ygyin.ojproblemservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ygyin.ojcommon.common.ErrorCode;
import com.ygyin.ojcommon.constant.CommonConstant;
import com.ygyin.ojcommon.exception.BusinessException;
import com.ygyin.ojcommon.exception.ThrowUtils;
import com.ygyin.ojcommon.utils.SqlUtils;
import com.ygyin.ojmodel.model.dto.problem.ProblemQueryRequest;
import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.entity.User;
import com.ygyin.ojmodel.model.vo.ProblemVO;
import com.ygyin.ojmodel.model.vo.UserVO;
import com.ygyin.ojproblemservice.mapper.ProblemMapper;
import com.ygyin.ojproblemservice.service.ProblemService;
import com.ygyin.ojservicecli.service.UserServiceFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yg
 * @description 针对表【problem(题目)】的数据库操作Service实现
 * @createDate 2024-03-30 18:11:42
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem>
        implements ProblemService {
    @Resource
    private UserServiceFeignClient userServiceFeignClient;

    /**
     * 用于校验题目是否合法
     *
     * @param problem
     * @param add
     */
    @Override
    public void validProblem(Problem problem, boolean add) {
        if (problem == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = problem.getTitle();
        String content = problem.getContent();
        String res = problem.getRes();
        String tags = problem.getTags();
        String testCase = problem.getTestCase();
        String testConfig = problem.getTestConfig();

        // 创建时，参数不能为空
        // 题目标题，内容和标签不能为空，空抛出异常参数错误
        if (add)
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);

        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目标题过长");

        if (StringUtils.isNotBlank(content) && content.length() > 8192)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目内容过长");

        if (StringUtils.isNotBlank(res) && res.length() > 8192)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目答案过长");

        if (StringUtils.isNotBlank(testCase) && testCase.length() > 8192)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题测试用例过长");

        if (StringUtils.isNotBlank(testConfig) && testConfig.length() > 8192)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置信息过长");

    }

    /**
     * 获取查询包装类，用户会使用特定字段进行查询，根据前端传来的请求对象来获取 MyBatis 的查询 QueryWrapper 类
     *
     * @param problemQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Problem> getQueryWrapper(ProblemQueryRequest problemQueryRequest) {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        if (problemQueryRequest == null) {
            return queryWrapper;
        }

        Long id = problemQueryRequest.getId();
        String title = problemQueryRequest.getTitle();
        String content = problemQueryRequest.getContent();
        String res = problemQueryRequest.getRes();
        List<String> tags = problemQueryRequest.getTags();
        Long userId = problemQueryRequest.getUserId();
        String sortField = problemQueryRequest.getSortField();
        String sortOrder = problemQueryRequest.getSortOrder();

        // 考虑用户可能根据什么字段进行查询
        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // 管理员可能通过答案查询
        queryWrapper.like(StringUtils.isNotBlank(res), "content", res);
        if (CollUtil.isNotEmpty(tags))
            for (String tag : tags)
                queryWrapper.like("tags", "\"" + tag + "\"");


        // 若根据 id 查询，则看用户传的 id 和数据库中的 id 是否一致
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 查询对应用户信息
     *
     * @param problem
     * @param request
     * @return
     */
    @Override
    public ProblemVO getProblemVO(Problem problem, HttpServletRequest request) {
        // 工具方法用于将 problem 实体对象快速转换为一个 VO 对象
        ProblemVO problemVO = ProblemVO.objToVo(problem);
        // 1. 关联查询用户信息，根据题目用户的 id 去查用户表得到对应用户实体对象
        Long userId = problem.getUserId();
        User user = null;
        if (userId != null && userId > 0)
            user = userServiceFeignClient.getById(userId);

        // 得到创建该题目的用户的脱敏信息，再填充到返回给前端的用户封装类里
        UserVO userVO = userServiceFeignClient.getUserVO(user);
        problemVO.setUserVO(userVO);

        /*
        2. 已登录，获取用户点赞、收藏状态
        User loginUser = userServiceFeignClient.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<ProblemThumb> problemThumbQueryWrapper = new QueryWrapper<>();
            problemThumbQueryWrapper.in("problemId", problemId);
            problemThumbQueryWrapper.eq("userId", loginUser.getId());
            ProblemThumb problemThumb = problemThumbMapper.selectOne(problemThumbQueryWrapper);
            problemVO.setHasThumb(problemThumb != null);
            // 获取收藏
            QueryWrapper<ProblemFavour> problemFavourQueryWrapper = new QueryWrapper<>();
            problemFavourQueryWrapper.in("problemId", problemId);
            problemFavourQueryWrapper.eq("userId", loginUser.getId());
            ProblemFavour problemFavour = problemFavourMapper.selectOne(problemFavourQueryWrapper);
            problemVO.setHasFavour(problemFavour != null);
        }
        */
        return problemVO;
    }

    /**
     * 根据问题的分页得到问题分页的 VO 包装类，类似为循环调用 getProblemVO
     * @param problemPage
     * @param request
     * @return
     */
    @Override
    public Page<ProblemVO> getProblemVOPage(Page<Problem> problemPage, HttpServletRequest request) {
        List<Problem> problemList = problemPage.getRecords();
        Page<ProblemVO> problemVOPage = new Page<>(problemPage.getCurrent(), problemPage.getSize(), problemPage.getTotal());
        if (CollUtil.isEmpty(problemList)) {
            return problemVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = problemList.stream().map(Problem::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userServiceFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> problemIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> problemIdHasFavourMap = new HashMap<>();
//        User loginUser = userServiceFeignClient.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> problemIdSet = problemList.stream().map(Problem::getId).collect(Collectors.toSet());
//            loginUser = userServiceFeignClient.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<ProblemThumb> problemThumbQueryWrapper = new QueryWrapper<>();
//            problemThumbQueryWrapper.in("problemId", problemIdSet);
//            problemThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<ProblemThumb> problemProblemThumbList = problemThumbMapper.selectList(problemThumbQueryWrapper);
//            problemProblemThumbList.forEach(problemProblemThumb -> problemIdHasThumbMap.put(problemProblemThumb.getProblemId(), true));
//            // 获取收藏
//            QueryWrapper<ProblemFavour> problemFavourQueryWrapper = new QueryWrapper<>();
//            problemFavourQueryWrapper.in("problemId", problemIdSet);
//            problemFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<ProblemFavour> problemFavourList = problemFavourMapper.selectList(problemFavourQueryWrapper);
//            problemFavourList.forEach(problemFavour -> problemIdHasFavourMap.put(problemFavour.getProblemId(), true));
//        }
        // 填充信息
        List<ProblemVO> problemVOList = problemList.stream().map(problem -> {
            ProblemVO problemVO = ProblemVO.objToVo(problem);
            Long userId = problem.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId))
                user = userIdUserListMap.get(userId).get(0);

            problemVO.setUserVO(userServiceFeignClient.getUserVO(user));
            return problemVO;
        }).collect(Collectors.toList());
        problemVOPage.setRecords(problemVOList);
        return problemVOPage;
    }
}




