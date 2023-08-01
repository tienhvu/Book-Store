package com.example.demo.Controller;

import com.example.demo.Config.AdminAccessOnly;
import com.example.demo.Entity.Book;
import com.example.demo.Entity.Order;
import com.example.demo.Entity.User;
import com.example.demo.Service.BookService;
import com.example.demo.Service.OrderService;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    @PostMapping("")
    public String createOrder(HttpSession session, @RequestParam("bookId") Long bookId,
                              @RequestParam("quantity") Long quantity, RedirectAttributes redirectAttributes) {
        // Lấy thông tin người dùng từ session
        User user = (User) session.getAttribute("user");

        // Lấy đối tượng Book từ bookId
        Book book = bookService.getBookById(bookId);

        // Lấy số lượng sách đã bán hiện tại
        Long currentQuantitySold = book.getQuantitySold() != null ? book.getQuantitySold() : 0L;

        // Tính toán số lượng sách đã bán mới
        long newQuantitySold = currentQuantitySold + quantity;

        // Cập nhật số lượng sách đã bán mới trong trường quantity_sold của bảng book
        book.setQuantitySold(newQuantitySold);
        bookService.saveBook(book);

        // Tạo đối tượng Order và gán các thông tin từ request parameters
        Order order = new Order();
        order.setBookId(bookId);
        order.setUserId(user.getId());
        order.setBookName(book.getTitle());
        order.setAuthor(book.getAuthor());
        order.setPrice((long) book.getPrice());
        order.setQuantity(quantity);
        order.setTotalPrice(quantity, (long) book.getPrice()); // Tính toán total_price dựa trên giá sách và số lượng

        // Lưu đơn hàng vào CSDL
        orderService.savedOrder(order);

        // Thêm thông báo thành công vào flash attribute
        redirectAttributes.addFlashAttribute("successMessage", "Thêm đơn hàng thành công!");

        // Chuyển hướng về trang thành công hoặc trang xác nhận đơn hàng
        return "redirect:/order/success";
    }

    @GetMapping("/success")
    public String showOrderSuccessPage(Model model, HttpSession session) {
        // Lấy thông báo thành công từ flash attribute
        String successMessage = (String) model.getAttribute("successMessage");
        // Đưa thông báo thành công vào model để hiển thị trên trang
        model.addAttribute("successMessage", successMessage);

        // Lấy thông tin người dùng từ session
        User user = (User) session.getAttribute("user");
        // Lấy danh sách đơn hàng của người dùng từ CSDL
        List<Order> orders = orderService.getOrderById(user.getId());
        // Đưa danh sách đơn hàng vào model để hiển thị trên trang
        model.addAttribute("orders", orders);

        // Trả về tên view của trang thành công
        return "orderSuccess";
    }

    @GetMapping("/deleteOrder/{id}")
    public String deleteOrder(HttpSession session, @PathVariable("id") long id, RedirectAttributes ra) {
        // Lấy thông tin người dùng từ session
        User user = (User) session.getAttribute("user");
        List<Order> orders = orderService.getOrderById(user.getId());

        // Tìm đơn hàng cần xóa dựa trên id
        Order deletedOrder = null;
        for (Order order : orders) {
            if (order.getId() == id) {
                deletedOrder = order;
                break;
            }
        }

        // Kiểm tra xem đơn hàng đã tìm thấy hay chưa
        if (deletedOrder == null) {
            // Nếu không tìm thấy đơn hàng, xử lý lỗi hoặc hiển thị thông báo cho người dùng
            System.out.println("Không tìm thấy đơn hàng");
            // ...
        } else {
            // Lấy thông tin về cuốn sách liên quan đến đơn hàng
            Book book = bookService.getBookById(deletedOrder.getBookId());

            // Lấy số lượng sách đã bán hiện tại
            Long currentQuantitySold = book.getQuantitySold() != null ? book.getQuantitySold() : 0L;

            // Giảm giá trị số lượng sách đã bán
            Long deletedOrderQuantity = deletedOrder.getQuantity();
            Long newQuantitySold = currentQuantitySold - deletedOrderQuantity;

            // Cập nhật số lượng sách đã bán mới trong trường quantity_sold của bảng book
            book.setQuantitySold(newQuantitySold);
            bookService.saveBook(book);

            // Xóa đơn hàng từ CSDL
            orderService.deleteOrderById(id);

            // Thêm thông báo thành công vào flash attribute
            ra.addFlashAttribute("message", "Xóa thành công!");
        }

        // Chuyển hướng về trang thành công hoặc trang xác nhận đơn hàng
        return "redirect:/order/success";
    }


    @GetMapping("/detail/{bookId}")
    public String showBookDetailPage(@PathVariable("bookId") Long bookId, Model model) {
        // Lấy thông tin sách từ bookId
        Book book = bookService.getBookById(bookId);
        // Đưa thông tin sách vào model để hiển thị trên trang
        model.addAttribute("book", book);
        // Trả về tên view của trang chi tiết sách
        return "bookDetail";
    }
}
