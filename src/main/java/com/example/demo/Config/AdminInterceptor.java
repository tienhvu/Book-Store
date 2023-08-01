package com.example.demo.Config;

import com.example.demo.Entity.User;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AdminAccessOnly annotation = handlerMethod.getMethodAnnotation(AdminAccessOnly.class);
            if (annotation != null) {
                // Kiểm tra trạng thái đăng nhập của người dùng
                User user = (User) request.getSession().getAttribute("user");
                if (user == null) {
                    // Người dùng chưa đăng nhập, chuyển hướng về trang đăng nhập
                    response.sendRedirect("/login");
                    return false;
                }

                // Kiểm tra vai trò của người dùng
                if (!userService.checkRole(user.getRole())) {
                    // Người dùng không có quyền truy cập, chuyển hướng về trang khác (ví dụ: trang home)
                    response.sendRedirect("/home");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Không cần thực hiện gì sau khi xử lý
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Không cần thực hiện gì sau khi hoàn tất
    }
}
