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

    @Autowired
    DefaultKaptcha defaultKaptcha;

    private final static String SHIRO_VERIFY_SESSION = "verification_session_key";


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
    public String loginPost(String username, String password, Model model, boolean rememberMe,String verifyCode) {
        /**
         * 使用shiro编写认证操作
         */
        //获取subject
        Subject subject = SecurityUtils.getSubject();
        //封装用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        String verifyCodeFromSession = (String) subject.getSession().getAttribute(SHIRO_VERIFY_SESSION);
        if ("".equals(verifyCode) || verifyCode.equals(verifyCodeFromSession)){
            model.addAttribute("msg","验证码错误");
            return "/login";
        }

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

    @GetMapping("/getCode")
    public void getGifCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
        byte[] verByte = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            request.getSession().setAttribute(SHIRO_VERIFY_SESSION, createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        verByte = jpegOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(verByte);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

}
