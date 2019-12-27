package com.danbro.shiro.dynamic.configuration.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Classname ShiroController
 * @Description TODO 前端控制器
 * @Date 2019/12/26 10:54
 * @Author Danrbo
 */
@Controller
public class ShiroController {

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/user/add")
    public String add(){
        return "/user/add";
    }

    @GetMapping("/user/update")
    public String update(){
        return "/user/update";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/unauth")
    public String unauth(){
        return "unauth";
    }

    @ResponseBody
    //能访问此url的角色
    @RequiresRoles("normal")
    @GetMapping("/user/normal")
    public String normal(){
        return "normal";
    }

    @ResponseBody
    @RequiresRoles("vip")
    @GetMapping("/user/vip")
    public String vip(){
        return "vip";
    }

    @RequiresRoles("supervip")
    @ResponseBody
    @GetMapping("/user/supervip")
    public String supervip(){
        return "supervip";
    }

    @GetMapping("/unauthorized")
    public String error(){
        return "unauthorized";
    }

    @GetMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "/index";
    }

    @PostMapping("/login")
    public String loginPost(String username, String password, Model model){
        /**
         * 使用shiro编写认证操作
         */
        //获取subject
        Subject subject = SecurityUtils.getSubject();
        //封装用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        //登录操作
        try {
            subject.login(token);
            return "redirect:/index";
        }
        //用户不存在
        catch (UnknownAccountException e){
            model.addAttribute("msg","用户不存在");
            return "/login";
        }
        //账户存在 密码错误
        catch (IncorrectCredentialsException e){
            model.addAttribute("msg","密码错误");
            return "/login";
        }

    }



}
