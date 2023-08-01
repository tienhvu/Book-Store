package com.example.demo.Repository;

import com.example.demo.Entity.Book;
import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    @Query("SELECT b FROM Book b ORDER BY b.releaseDate DESC")
    List<Book> findAllByReleaseDateDesc(); // Lấy tất cả sách và sắp xếp theo thời gian thêm mới nhất

    @Query("SELECT b FROM Book b ORDER BY b.quantitySold DESC")
    List<Book> findAllByQuantityDesc(); // Lấy tất cả sách và sắp xếp theo số lượng sách đã bán giảm dần
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String titleKeyword, String authorKeyword);
    public Book findByTitle(String title);

    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);

    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

}
