package com.danbro.shiro.dynamic.configuration.controller;

import com.danbro.shiro.dynamic.configuration.enity.User;
import com.danbro.shiro.dynamic.configuration.service.UserService;
import com.danbro.shiro.dynamic.configuration.utils.PasswordGenerateUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Classname ShiroController
 * @Description TODO 前端控制器
 * @Date 2019/12/26 10:54
 * @Author Danrbo
 */
@Controller
public class ShiroController {

    @Autowired
    UserService userService;



    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/addUserTest")
    public String addTest() {
        User user = new User();
        user.setUsername("user1");
        user.setSalt(Long.toString(System.currentTimeMillis()));
        user.setPerms("user:add");
        user.setRole("vip");
        user.setPassword(PasswordGenerateUtil.getPassword(user.getUsername(), "123", user.getSalt(), 2));
        userService.addUser(user);
        return "/user/add";
    }

    @GetMapping("/user/add")
    public String add() {
        return "/user/add";
    }

    @GetMapping("/user/update")
    public String update() {
        return "/user/update";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/unauth")
    public String unauth() {
        return "unauth";
    }

    @ResponseBody
    //能访问此url的角色
    @RequiresRoles("normal")
    @GetMapping("/user/normal")
    public String normal() {
        return "normal";
    }

    @ResponseBody
    @RequiresRoles("vip")
    @GetMapping("/user/vip")
    public String vip() {
        return "vip";
    }

    @RequiresRoles("supervip")
    @ResponseBody
    @GetMapping("/user/supervip")
    public String supervip() {
        return "supervip";
    }

    @GetMapping("/unauthorized")
    public String error() {
        return "unauthorized";
    }

    @GetMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "/index";
    }

    @PostMapping("/login")
    public String loginPost(String username, String password, Model model, boolean rememberMe) {
        /**
         * 使用shiro编写认证操作
         */
        //获取subject
        Subject subject = SecurityUtils.getSubject();
        //封装用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        //登录操作
        try {
            //rememberMe设置
            token.setRememberMe(rememberMe);
            subject.login(token);
            return "redirect:/index";
        }
        //用户不存在
        catch (UnknownAccountException e) {
            model.addAttribute("msg", "用户不存在");
            return "/login";
        }
        //账户存在 密码错误
        catch (IncorrectCredentialsException e) {
            model.addAttribute("msg", "密码错误");
            return "/login";
        }
    }
}
