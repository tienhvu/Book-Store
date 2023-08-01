package com.example.demo.Service;

import com.example.demo.Entity.Order;
import com.example.demo.Entity.Review;

import java.util.List;

public interface ReviewService {
    void savedReview(Review review);
    List<Review> getAllReviews();
    List<Review> getReviewsByBookId(Long bookId);
}
