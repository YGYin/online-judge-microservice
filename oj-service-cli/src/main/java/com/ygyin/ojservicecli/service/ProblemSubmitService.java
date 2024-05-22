package com.ygyin.ojservicecli.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ygyin.ojmodel.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.ygyin.ojmodel.model.dto.problemsubmit.ProblemSubmitQueryRequest;
import com.ygyin.ojmodel.model.entity.ProblemSubmit;
import com.ygyin.ojmodel.model.entity.User;
import com.ygyin.ojmodel.model.vo.ProblemSubmitVO;

/**
* @author yg
* @description 针对表【problem_submit(题目提交)】的数据库操作Service
* @createDate 2024-03-30 18:15:12
*/
public interface ProblemSubmitService extends IService<ProblemSubmit> {

    /**
     * 提交题目
     *
     * @param problemSubmitAddRequest 题目提交请求的信息
     * @param loginUser
     * @return 题目提交记录的 id
     */
    long doProblemSubmit(ProblemSubmitAddRequest problemSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param problemSubmitQueryRequest
     * @return
     */
    QueryWrapper<ProblemSubmit> getQueryWrapper(ProblemSubmitQueryRequest problemSubmitQueryRequest);


    /**
     * 获取提交题目的封装做脱敏
     *
     * @param problemSubmit
     * @param loginUser
     * @return
     */
    ProblemSubmitVO getProblemSubmitVO(ProblemSubmit problemSubmit, User loginUser);

    /**
     * 分页获取提交题目的封装
     *
     * @param problemSubmitPage
     * @param loginUser
     * @return
     */
    Page<ProblemSubmitVO> getProblemSubmitVOPage(Page<ProblemSubmit> problemSubmitPage, User loginUser);

}
