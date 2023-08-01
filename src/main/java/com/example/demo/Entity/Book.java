package com.example.demo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Size(min = 2,max = 50,message="Tiêu đề tối thiểu 2 kí tự")
    private String title;
    @Column(nullable = false)
    @Size(min=2, max = 50,message="Tên tác giả tối thiểu 2 kí tự")
    private String author;
    @Column
    private String description;
    @Column
    private String category;
    @Column(nullable = false)
    private LocalDate releaseDate;
    @Column
    private int pageCount;
    @Column
    private Long quantitySold;
    @Column(nullable = true, length= 64)
    private String photos;
    @Column
    private Long price;


    @Transient
    public String getPhotosImagePath(){
        if (photos == null || id == null) return null;
        return "/book-photos/" + id + "/" + photos;
    }




    public Book() {
        this.releaseDate = LocalDate.now(); // Gán giá trị mặc định là ngày hiện tại
    }


    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(long quantitySold) {
        this.quantitySold = quantitySold;
    }
    public Long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
