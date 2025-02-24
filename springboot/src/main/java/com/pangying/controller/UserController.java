package com.pangying.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pangying.entity.File;
import com.pangying.entity.R;
import com.pangying.entity.User;
import com.pangying.service.IFileService;
import com.pangying.service.IUserService;
import com.pangying.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-23
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IFileService fileService;

    @GetMapping("/list")
    public R list() {
        return R.success(userService.list());
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return userService.removeById(id) ? R.success() : R.fail("删除失败");
    }

    @PostMapping
    public R add(@RequestBody User user) {
        String avatar = fileService.getFilePathById(user.getId(), "avatar");
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        user.setPasswordHash(MD5Utils.encode("123456"));
        return userService.save(user) ? R.success() : R.fail("添加失败");
    }

    @PostMapping("/update")
    public R update(@RequestBody User user) {
        String avatar = fileService.getFilePathById(user.getId(), "avatar");
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        return userService.updateById(user) ? R.success() : R.fail("更新失败");
    }
}
