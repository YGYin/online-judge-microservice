package com.ygyin.ojuserservice.controller.inner;

import com.ygyin.ojmodel.model.entity.User;
import com.ygyin.ojservicecli.service.UserServiceFeignClient;
import com.ygyin.ojuserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 仅供内部服务之间调用用户服务的 user controller
 * 全局路径加上了 /api/user
 */
@RestController("/inner")
public class InnerUserController implements UserServiceFeignClient {

    @Resource
    private UserService userService;
    /**
     * 根据用户 id 获取用户
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    };

    /**
     * 根据用户多个 id 的列表获取用户列表
     * @param userIdList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("userIdList") Collection<Long> userIdList) {
        return userService.listByIds(userIdList);
    };

}
