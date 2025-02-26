package com.pangying.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pangying.entity.R;
import com.pangying.entity.User;
import com.pangying.service.IUserService;
import com.pangying.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public R login(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,user.getEmail());
        User one = userService.getOne(wrapper);
        if (one == null) {
            return R.fail("用户不存在");
        }
        if (!one.getPasswordHash().equals(MD5Utils.encode(user.getPasswordHash()))) {
            return R.fail("密码错误");
        }
        return R.success(one);
    }

    @PostMapping("/register")
    public R register(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,user.getEmail());
        User one = userService.getOne(wrapper);
        if (one != null) {
            return R.fail("用户已存在");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setAvatar("https://img.yzcdn.cn/vant/cat.jpeg");
        user.setPasswordHash(MD5Utils.encode(user.getPasswordHash()));
        user.setRole("user");
        user.setGender("保密");
        user.setStatus("active");
        return userService.save(user) ? R.success() : R.fail("注册失败");
    }

    @GetMapping("/getProfile")
    public R getProfile(@RequestParam Long id) {
        return R.success(userService.getById(id));
    }

    @PostMapping("/updateProfile")
    public R updateProfile(@RequestBody User user) {
        User existingUser = userService.getById(user.getId());
        if (existingUser == null) {
            return R.fail("用户不存在");
        }

        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setGender(user.getGender());
        existingUser.setUpdatedAt(LocalDateTime.now());

        if (userService.updateById(existingUser)) {
            return R.success("更新成功");
        } else {
            return R.fail("更新失败");
        }
    }
}
