package com.ygyin.ojproblemservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ygyin.ojcommon.common.ErrorCode;
import com.ygyin.ojcommon.constant.CommonConstant;
import com.ygyin.ojcommon.exception.BusinessException;
import com.ygyin.ojcommon.utils.SqlUtils;
import com.ygyin.ojmodel.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.ygyin.ojmodel.model.dto.problemsubmit.ProblemSubmitQueryRequest;
import com.ygyin.ojmodel.model.entity.Problem;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.entity.User;
import com.ygyin.ojmodel.model.enums.ProblemLanguageEnum;
import com.ygyin.ojmodel.model.enums.ProblemSubmitStatusEnum;
import com.ygyin.ojmodel.model.vo.ProblemSubmitVO;
import com.ygyin.ojproblemservice.mapper.ProblemSubmitMapper;
import com.ygyin.ojproblemservice.service.ProblemService;
import com.ygyin.ojproblemservice.service.ProblemSubmitService;
import com.ygyin.ojservicecli.service.JudgeService;
import com.ygyin.ojservicecli.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author yg
 * @description 针对表【problem_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-03-30 18:15:12
 */
@Service
public class ProblemSubmitServiceImpl extends ServiceImpl<ProblemSubmitMapper, ProblemSubmit>
        implements ProblemSubmitService {

    @Resource
    private ProblemService problemService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param problemSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doProblemSubmit(ProblemSubmitAddRequest problemSubmitAddRequest, User loginUser) {
        // 校验 language 是否合法，编写枚举类
        // 先获取用户提交题目的语言，再根据语言的字符串 value 来映射获取对应的枚举值
        String language = problemSubmitAddRequest.getLanguage();
        ProblemLanguageEnum languageEnum = ProblemLanguageEnum.getEnumByValue(language);
        if (languageEnum == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未获取到对应的编程语言");

        long problemId = problemSubmitAddRequest.getProblemId();
        // 判断题目实体是否存在，获取题目信息
        Problem problem = problemService.getById(problemId);
        if (problem == null)
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);

        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目，新建 Submit 对象，并从请求参数中获取参数以设置 Submit 中的属性
        ProblemSubmit problemSubmit = new ProblemSubmit();
        problemSubmit.setUserId(userId);
        problemSubmit.setProblemId(problemId);
        problemSubmit.setCode(problemSubmitAddRequest.getCode());
        problemSubmit.setLanguage(language);

        // 设置 status 为初始 Waiting 状态，编写枚举类
        problemSubmit.setTestStatus(ProblemSubmitStatusEnum.WAITING_FOR_JUDGE.getValue());
        // 暂时设置为一个空 json
        problemSubmit.setTestInfo("{}");
        boolean isSaved = this.save(problemSubmit);
        if (!isSaved)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用于提交的题目数据插入失败");

        // 获取提交题目 id，异步执行判题服务
        Long problemSubmitId = problemSubmit.getId();
        CompletableFuture.runAsync(() -> {
            judgeService.doJudgeProblem(problemSubmitId);
        });

        return problemSubmitId;
        // 锁必须要包裹住事务方法，要么限流，要么此处锁加事务防止用户重复提交
        // 判题机性能有限，避免用户多次点击提交，保证一个用户同时只能提交一条
//        ProblemSubmitService problemSubmitService = (ProblemSubmitService) AopContext.currentProxy();
//        synchronized (String.valueOf(userId).intern()) {
//            return problemSubmitService.doProblemSubmitInner(userId, problemId);
    }


    /**
     * 获取查询包装类，用户会使用特定字段进行查询，根据前端传来的请求对象来拼接出一个 MyBatis 的查询 QueryWrapper 对象
     *
     * @param problemSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ProblemSubmit> getQueryWrapper(ProblemSubmitQueryRequest problemSubmitQueryRequest) {
        QueryWrapper<ProblemSubmit> queryWrapper = new QueryWrapper<>();
        if (problemSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = problemSubmitQueryRequest.getLanguage();
        Long problemId = problemSubmitQueryRequest.getProblemId();
        Long userId = problemSubmitQueryRequest.getUserId();
        Integer testStatus = problemSubmitQueryRequest.getTestStatus();


        // 若根据 language 查询，则看用户传的 language 和数据库中的 language 是否一致
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(problemId), "problemId", problemId);
        queryWrapper.eq(ProblemSubmitStatusEnum.getEnumByValue(testStatus) != null, "testStatus", testStatus);
        String sortField = problemSubmitQueryRequest.getSortField();
        String sortOrder = problemSubmitQueryRequest.getSortOrder();

        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 查询对应用户信息
     *
     * @param problemSubmit
     * @param loginUser
     * @return
     */
    @Override
    public ProblemSubmitVO getProblemSubmitVO(ProblemSubmit problemSubmit, User loginUser) {
        // 优化接口参数为 User，避免 getProblemSubmitVOPage 多次调用 request 访问本地数据库
        // 工具方法用于将 problemSubmit 实体对象快速转换为一个 VO 对象
        ProblemSubmitVO problemSubmitVO = ProblemSubmitVO.objToVo(problemSubmit);

        // 获取当前登录用户的用户 id
        long userId = loginUser.getId();
        // 如果题目不是本人提交的且不是管理员账户，对返回给前端的 VO 中的代码信息进行脱敏
        if (userId != problemSubmit.getUserId() && !userService.isAdmin(loginUser))
            problemSubmitVO.setCode(null);

        return problemSubmitVO;
    }

    /**
     * 根据提交问题的分页得到问题分页的 VO 包装类，类似为循环调用 getProblemSubmitVO
     *
     * @param problemSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<ProblemSubmitVO> getProblemSubmitVOPage(Page<ProblemSubmit> problemSubmitPage, User loginUser) {
        List<ProblemSubmit> problemSubmitList = problemSubmitPage.getRecords();
        Page<ProblemSubmitVO> problemSubmitVOPage = new Page<>(problemSubmitPage.getCurrent(), problemSubmitPage.getSize(), problemSubmitPage.getTotal());
        if (CollUtil.isEmpty(problemSubmitList)) {
            return problemSubmitVOPage;
        }


        List<ProblemSubmitVO> problemSubmitVOList = problemSubmitList.stream()
                .map(problemSubmit -> { return getProblemSubmitVO(problemSubmit, loginUser);})
                .collect(Collectors.toList());

        problemSubmitVOPage.setRecords(problemSubmitVOList);
        return problemSubmitVOPage;
    }
}




