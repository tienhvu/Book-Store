package com.example.demo.Controller;

import com.example.demo.Entity.Book;
import com.example.demo.Entity.User;
import com.example.demo.Service.BookService;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthenController {
    private BookService bookService;
    private UserService userService;

    @Autowired
    public AuthenController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        // Kiểm tra tài khoản
        User user = userService.findUserByEmail(email);
        if (user == null) {
            // Sai email, thông báo lỗi
            model.addAttribute("error", "Email không hợp lệ!");
            return "login";
        }

        // Kiểm tra mật khẩu
        boolean isAuthenticated = userService.checkPasswordUser(email, password);
        if (!isAuthenticated) {
            // Sai mật khẩu, thông báo lỗi
            model.addAttribute("error1", "Sai mật khẩu!");
            return "login";
        }

        session.setAttribute("user", user);

        // Kiểm tra vai trò của người dùng
        if (userService.checkRole(user.getRole())) {
            // Nếu là vai trò admin, chuyển hướng tới trang quản lí
            return "redirect:/admin";
        } else {
            // Nếu là vai trò user, chuyển hướng tới trang user
            return "redirect:/user/home";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Tạo đối tượng model để lưu trữ dữ liệu form
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(user.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "Email đã được sử dụng!");
        }

        // Kiểm tra sự tồn tại của username
        User existingUsernameUser = userService.findUserByUsername(user.getUsername());

        if (existingUsernameUser != null && existingUsernameUser.getUsername() != null && !existingUsernameUser.getUsername().isEmpty()) {
            result.rejectValue("username", null, "Tên đăng nhập đã được sử dụng!");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Lấy thông tin người dùng từ phiên làm việc
        User user = (User) session.getAttribute("user");

        // Xóa thông tin người dùng khỏi phiên làm việc
        session.removeAttribute("user");

        // Kiểm tra vai trò của người dùng
        if (user != null && userService.checkRole(user.getRole())) {
            // Nếu là vai trò admin, chuyển hướng về trang adminLogout
            return "redirect:/adminLogout";
        } else {
            // Nếu là vai trò user hoặc thông tin người dùng không có sẵn, chuyển hướng về trang index
            return "redirect:/home";
        }
    }
    @GetMapping("/adminLogout")
    public String adminLogout(@RequestParam(value = "keyword", required = false) String keyword,Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            List<Book> books = bookService.searchBooksByKeyword(keyword);
            model.addAttribute("keyword", keyword);
            model.addAttribute("books", books);
        } else {
            List<Book> books = bookService.getAll();
            model.addAttribute("books", books);
        }
        return "adminLogout";
    }
}
