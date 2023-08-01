package com.example.demo.Controller;
import com.example.demo.Config.AdminAccessOnly;
import com.example.demo.Entity.Book;
import com.example.demo.Entity.Order;
import com.example.demo.Service.BookService;
import com.example.demo.Service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private BookService bookService;
    @Autowired
    private OrderService orderService;

    public AdminController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("")
    @AdminAccessOnly
    public String adminPage(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            List<Book> books = bookService.searchBooksByKeyword(keyword);
            model.addAttribute("keyword", keyword);
            model.addAttribute("books", books);
        } else {
            List<Book> books = bookService.getAll();
            model.addAttribute("books", books);
        }
        return "admin";
    }


    @GetMapping("/addBook")
    @AdminAccessOnly
    public String showAddBookPage(@RequestParam(value = "view", required = false) Boolean view,
                                  @RequestParam(value = "bookId", required = false) Long bookId,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        Book book;
        boolean isEditMode = false; // Biến để xác định chế độ là View hay Edit
        if (redirectAttributes.getFlashAttributes().containsKey("errorMessage")) {
            model.addAttribute("errorMessage", redirectAttributes.getFlashAttributes().get("errorMessage"));
        }
        if (view != null && view) {
            // Trường hợp chế độ View hoặc Edit
            if (bookId != null) {
                book = bookService.getBookById(bookId);
                if (book == null) {
                    // Xử lý khi không tìm thấy cuốn sách cần xem/sửa
                    return "redirect:/admin";
                }
                isEditMode = true; // Chuyển sang chế độ Edit
            } else {
                // Trường hợp không có bookId được cung cấp
                return "redirect:/admin";
            }
        } else {
            // Trường hợp chế độ Add Book
            book = new Book();
        }

        model.addAttribute("book", book);
        model.addAttribute("isEditMode", isEditMode);

        return "Add_Book";
    }

    @PostMapping("/saveBook")
    @AdminAccessOnly
    public String saveBook(HttpServletRequest request,
                           @RequestParam(value = "bookId", required = false) Long bookId,
                           @Valid @ModelAttribute(name = "book") Book book,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           @RequestParam(value = "view", required = false) Boolean view,
                           @RequestParam(value = "isEditMode", required = false, defaultValue = "false") boolean isEditMode,
                           @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {

        model.addAttribute("isEditMode", isEditMode);
        FieldError titleError = result.getFieldError("title");
        FieldError authorError = result.getFieldError("author");
        if (!isEditMode) {
            // Trường hợp chế độ "Thêm"
            if (result.hasErrors()) {
                // Kiểm tra các lỗi cụ thể
                if (titleError != null) {
                    model.addAttribute("errorMessage", "Lỗi: " + titleError.getDefaultMessage());
                } else if (authorError != null) {
                    model.addAttribute("errorMessage", "Lỗi: " + authorError.getDefaultMessage());
                } else {
                    model.addAttribute("errorMessage", "Cuốn sách này đã tồn tại!");
                }
                return "Add_Book";
            }

            if (!book.getTitle().equals("") && !book.getAuthor().equals("")) {
                boolean bookExists = bookService.existsByTitleAndAuthor(book.getTitle(), book.getAuthor());
                if (bookExists) {
                    model.addAttribute("errorMessage", "Cuốn sách này đã tồn tại!");
                    return "Add_Book";
                }
            }

            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                book.setPhotos(fileName);

                // Thêm mới sách
                Book newBook = bookService.saveBook(book);
                // Xử lý tải lên và cập nhật hình ảnh cho cuốn sách mới
                String uploadDir = "./book-photos/" + newBook.getId();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IOException("Không thể lưu tệp tải lên: " + fileName);
                }
            } else {
                // Trường hợp không có tệp tin được chọn
                // Thêm mới sách mà không cập nhật ảnh
                bookService.saveBook(book);
            }
            // Trả về thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Thêm thành công!");
            return "redirect:/admin";
        }

        if (result.hasErrors()) {
            // Kiểm tra các lỗi cụ thể
            String errorHtml;
            if (titleError != null) {
                model.addAttribute("errorMessage", "Lỗi: " + titleError.getDefaultMessage());
                errorHtml = "<div class=\"error\"><p style=\"color: red; text-align: center;\">" + model.getAttribute("errorMessage") + "</p></div>";
            } else if (authorError != null) {
                model.addAttribute("errorMessage", "Lỗi: " + authorError.getDefaultMessage());
                errorHtml = "<div class=\"error\"><p style=\"color: red; text-align: center;\">" + model.getAttribute("errorMessage") + "</p></div>";
            } else {
                model.addAttribute("errorMessage", "Có lỗi xảy ra!");
                errorHtml = "<div class=\"error\"><p style=\"color: red; text-align: center;\">" + model.getAttribute("errorMessage") + "</p></div>";
            }
            model.addAttribute("errorHtml", errorHtml);
            return "Add_Book";
        }

        if (isEditMode && bookId != null) {
            // Lấy thông tin cuốn sách cần sửa
            Book savedBook = bookService.getBookById(bookId);
            System.out.println("savebook: " + savedBook);
            if (savedBook.getId() == null) {
                // Xử lý khi không tìm thấy cuốn sách cần sửa
                return "redirect:/admin";
            }

            // Kiểm tra xem tên và tác giả mới có trùng với cuốn sách khác hay không
            List<Book> existingBooks = bookService.getBooksByTitleAndAuthorIgnoreCase(book.getTitle(), book.getAuthor());
            existingBooks = existingBooks.stream()
                    .filter(b -> b.getId() != bookId)
                    .collect(Collectors.toList());

            if (!existingBooks.isEmpty()) {
                // Tên và tác giả mới trùng với cuốn sách khác
                System.out.println("Tên và tác giả đã tồn tại");
                model.addAttribute("errorMessage", "Tên và tác giả đã tồn tại!");
                String errorHtml = "<div class=\"error\"><p style=\"color: red; text-align: center;\">" + model.getAttribute("errorMessage") + "</p></div>";

                model.addAttribute("errorHtml", errorHtml);
                return "Add_Book";
            }
            System.out.println(book.getDescription());
            System.out.println(book.getCategory());
            // Cập nhật thông tin sách
            savedBook.setTitle(book.getTitle());
            savedBook.setAuthor(book.getAuthor());
            savedBook.setDescription(book.getDescription());
            savedBook.setReleaseDate(book.getReleaseDate());
            savedBook.setPageCount(book.getPageCount());
            savedBook.setPrice(book.getPrice());
            savedBook.setCategory(book.getCategory());

            if (multipartFile != null && !multipartFile.isEmpty()) {
                // Cập nhật ảnh mới
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                savedBook.setPhotos(fileName);

                // Xóa ảnh cũ của cuốn sách
                String oldFileName = savedBook.getPhotos();
                if (oldFileName != null && !oldFileName.isEmpty()) {
                    Path oldFilePath = Paths.get("./book-photos/" + savedBook.getId() + "/" + oldFileName);
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                }

                // Xử lý tải lên và cập nhật hình ảnh cho cuốn sách
                String uploadDir = "./book-photos/" + savedBook.getId();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IOException("Không thể lưu tệp tải lên: " + fileName);
                }
            }

            bookService.updateBook(savedBook);
            // Lấy danh sách đơn hàng liên quan đến cuốn sách
            List<Order> orders = orderService.getOrdersByBookId(bookId);
            for (Order order : orders) {
                // Cập nhật thông tin sách trong đơn hàng
                order.setBookName(book.getTitle());
                order.setAuthor(book.getAuthor());
                order.setPrice(book.getPrice());
                order.setTotalPrice(order.getQuantity(), order.getPrice());
                // Lưu lại các thay đổi trong đơn hàng
                orderService.savedOrder(order);
            }

            System.out.println("updateBook: success");
            // Trả về thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Sửa thành công!");
            return "redirect:/admin";
        } else {
            // Xử lý khi không tìm thấy cuốn sách cần sửa
            System.out.println("Lỗi");
            // Trường hợp chế độ "Sửa"
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy cuốn sách cần sửa");
            return "redirect:/admin";
        }
    }


    @GetMapping("/deleteBook/{bookId}")
    @AdminAccessOnly
    public String deleteBook(@PathVariable("bookId") long bookId, RedirectAttributes ra) {
        // Xóa cuốn sách khỏi CSDL
        bookService.deleteBookById(bookId);

        // Cập nhật danh sách đơn hàng của người dùng
        List<Order> orders = orderService.getOrdersByBookId(bookId);
        for (Order order : orders) {
            orderService.deleteOrderById(order.getId());
        }

        ra.addFlashAttribute("message", "Xóa thành công!");
        return "redirect:/admin";
    }


}