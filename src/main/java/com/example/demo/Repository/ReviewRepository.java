package com.example.demo.Repository;

import com.example.demo.Entity.Order;
import com.example.demo.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByBookId(Long bookId);
}
