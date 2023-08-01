package com.example.demo.Service;

import com.example.demo.Entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    List<Book> getAll();
    List<Book> getAllByReleaseDateDesc(); // Lấy tất cả sách và sắp xếp theo thời gian thêm mới nhất
    List<Book> getAllByQuantityDesc();
    Book saveBook(Book book);
    Book getBookById(long id);
    Book updateBook(Book book);

    void deleteBookById(long id);
    boolean existsByTitleAndAuthor(String title, String author);
    List<Book> searchBooksByKeyword(String keyword);
    List<Book> getBooksByTitleAndAuthorIgnoreCase(String title, String author);
//    Page<Book> findPagninated(int pageNo, int pageSize, String sortFiel);
}
