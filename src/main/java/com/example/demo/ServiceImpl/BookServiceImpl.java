package com.example.demo.ServiceImpl;

import com.example.demo.Entity.Book;
import com.example.demo.Repository.BookRepository;
import com.example.demo.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepo;

    public BookServiceImpl(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    public List<Book> getAll() {
        return bookRepo.findAll();
    }

    @Override
    public List<Book> getAllByReleaseDateDesc() {
        return bookRepo.findAllByReleaseDateDesc();
    }

    @Override
    public List<Book> getAllByQuantityDesc() {
        return bookRepo.findAllByQuantityDesc();
    }


    @Override
    public Book saveBook(Book book) {
        return this.bookRepo.save(book);
    }

    @Override
    public Book getBookById(long id) {
        Optional<Book> optional = bookRepo.findById(id);
        Book book = null;
        if(optional.isPresent()){
            book = optional.get();
        }else{
            throw new RuntimeException("Không tìm thấy sách với id :: "+ id);
        }
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        Long bookId = book.getId();
        String title = book.getTitle();
        String author = book.getAuthor();
        String description = book.getDescription();
        String category = book.getCategory();
        LocalDate  releaseDate= book.getReleaseDate();
        int pageCount = book.getPageCount();
        Long price = book.getPrice();


        // Kiểm tra xem tên và tác giả mới có trùng với bất kỳ cuốn sách nào khác hay không
        List<Book> existingBooks = bookRepo.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title,author);

        // Lọc bỏ cuốn sách hiện tại trong danh sách đã tìm được
        existingBooks = existingBooks.stream()
                .filter(b -> b.getId() != bookId)
                .collect(Collectors.toList());

        if (!existingBooks.isEmpty()) {
            // Tên và tác giả mới trùng với cuốn sách khác
            return null;
        }

        // Cuốn sách hợp lệ, tiến hành cập nhật thông tin và lưu vào CSDL
        Book existingBookToUpdate = bookRepo.findById(bookId).orElse(null);
        if (existingBookToUpdate != null) {
            existingBookToUpdate.setTitle(title);
            existingBookToUpdate.setAuthor(author);
            existingBookToUpdate.setPhotos(book.getPhotos());
            existingBookToUpdate.setDescription(book.getDescription());
            existingBookToUpdate.setCategory(book.getCategory());
            existingBookToUpdate.setReleaseDate(book.getReleaseDate());
            existingBookToUpdate.setPageCount(book.getPageCount());
            existingBookToUpdate.setPrice(book.getPrice());
            // Lưu cuốn sách đã được cập nhật vào CSDL
            Book updatedBook = bookRepo.save(existingBookToUpdate);
            return updatedBook;
        }

        return null;
    }


    @Override
    public void deleteBookById(long id) {
        bookRepo.deleteById(id);
    }

    @Override
    public boolean existsByTitleAndAuthor(String title, String author) {
        return bookRepo.existsByTitleIgnoreCaseAndAuthorIgnoreCase(title, author);
    }

    @Override
    public List<Book> searchBooksByKeyword(String keyword) {
        return bookRepo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Book> getBooksByTitleAndAuthorIgnoreCase(String title, String author) {
        return bookRepo.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author);
    }


}
