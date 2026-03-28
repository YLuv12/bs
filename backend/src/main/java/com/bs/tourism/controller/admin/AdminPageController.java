package com.bs.tourism.controller.admin;

import com.bs.tourism.entity.Admin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/page")
public class AdminPageController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping({ "/", "/index", "/dashboard" })
    public String indexPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/index";
    }

    @GetMapping("/admin")
    public String adminPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/admin";
    }

    @GetMapping("/role")
    public String rolePage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/role";
    }

    @GetMapping("/permission")
    public String permissionPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/permission";
    }

    @GetMapping("/user")
    public String userPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/user";
    }

    @GetMapping("/scenic")
    public String scenicPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/scenic";
    }

    @GetMapping("/food")
    public String foodPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/food";
    }

    @GetMapping("/comment")
    public String commentPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/comment";
    }

    @GetMapping("/news")
    public String newsPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/news";
    }

    @GetMapping("/feedback")
    public String feedbackPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/feedback";
    }

    @GetMapping("/config")
    public String configPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/config";
    }

    @GetMapping("/log")
    public String logPage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/log";
    }

    @GetMapping("/profile")
    public String profilePage(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        request.setAttribute("admin", admin);
        return "admin/profile";
    }
}
