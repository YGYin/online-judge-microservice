package com.ygyin.ojservicecli.service;

import com.ygyin.ojcommon.common.ErrorCode;
import com.ygyin.ojcommon.exception.BusinessException;
import com.ygyin.ojmodel.model.entity.User;
import com.ygyin.ojmodel.model.enums.UserRoleEnum;
import com.ygyin.ojmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.ygyin.ojcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 * name 定义服务提供者，告诉 Feign Client 提供服务的模块
 * 具体使用哪个服务需要对应的 mapping 注解
 */
@FeignClient(name = "oj-user-service", path = "/api/user/inner")
public interface UserServiceFeignClient {


    /**
     * 获取当前登录用户，因为要跨服务调用，传输过程需要序列化可能会丢失信息
     *
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        // 直接从 session 中获取用户信息，feign 没有查询数据库的能力
//        long userId = currentUser.getId();
//        currentUser = this.getById(userId);
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        return currentUser;
    }

    /**
     * 是否为管理员，默认方法，不需要依赖远程服务实现
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user){
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user){
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 根据用户 id 获取用户
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") long userId);

    /**
     * 根据用户多个 id 的列表获取用户列表
     * @param userIdList
     * @return
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("userIdList") Collection<Long> userIdList);

}
