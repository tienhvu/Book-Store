package com.example.demo.Controller;

import com.example.demo.Config.AdminAccessOnly;
import com.example.demo.Config.UserAccessOnly;
import com.example.demo.Entity.Book;
import com.example.demo.Entity.Review;
import com.example.demo.Entity.User;
import com.example.demo.Service.BookService;
import com.example.demo.Service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService;
    @GetMapping("/home")
    @UserAccessOnly
    public String userViewPage(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Book> newBooks = bookService.getAllByReleaseDateDesc();
        List<Book> popularBooks = bookService.getAllByQuantityDesc();

        if (keyword != null && !keyword.isEmpty()) {
            List<Book> searchedBooks = bookService.searchBooksByKeyword(keyword);
            model.addAttribute("keyword", keyword);
            model.addAttribute("books", searchedBooks);
        } else {
            model.addAttribute("newBooks", newBooks);
            model.addAttribute("popularBooks", popularBooks);
        }

        return "user";
    }
    @GetMapping("/viewDetail/{bookId}")
    public String viewBookDetail(@PathVariable Long bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        model.addAttribute("book", book);
        // Truy vấn danh sách đánh giá cho cuốn sách cụ thể từ CSDL và gán vào model
        List<Review> reviews = reviewService.getReviewsByBookId(bookId);
        model.addAttribute("reviews", reviews);
        // Chuyển hướng đến trang chi tiết sách
        return "viewBookDetail";
    }

    @PostMapping("/submit-review")
    public String submitReview(HttpSession session,
                               @RequestParam("rating") int rating,
                               @RequestParam("comment") String comment,
                               @RequestParam("bookId") Long bookId) {

        // Lấy thông tin người dùng từ session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Xử lý khi người dùng chưa đăng nhập
            // ...
        } else {
            // Lấy thông tin người dùng hiện tại
            Long userId = user.getId();
            String userName = user.getUsername();
            // Lưu đánh giá vào CSDL, ví dụ: sử dụng service hoặc repository để lưu đánh giá vào CSDL
            // Tạo một đối tượng Review từ dữ liệu nhận được
            Review review = new Review();
            review.setRating(rating);
            review.setComment(comment);
            review.setUserName(userName);
            review.setBookId(bookId);
            review.setUserId(userId);
            // Gọi service để lưu đánh giá vào CSDL
            reviewService.savedReview(review);
        }
        // Chuyển hướng đến trang chi tiết sách và gửi thông báo thành công
        return "redirect:/user/viewDetail/" + bookId ;
    }





}