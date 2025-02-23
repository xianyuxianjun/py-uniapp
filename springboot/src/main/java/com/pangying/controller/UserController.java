package com.pangying.controller;

import com.pangying.entity.R;
import com.pangying.entity.User;
import com.pangying.service.IUserService;
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
        return userService.save(user) ? R.success() : R.fail("添加失败");
    }

    @PostMapping("/update")
    public R update(@RequestBody User user) {
        return userService.updateById(user) ? R.success() : R.fail("更新失败");
    }

}
