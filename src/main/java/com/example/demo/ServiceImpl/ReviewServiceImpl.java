package com.example.demo.ServiceImpl;

import com.example.demo.Entity.Review;
import com.example.demo.Repository.ReviewRepository;
import com.example.demo.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;

    public ReviewServiceImpl(ReviewRepository reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    @Override
    public void savedReview(Review review) {
        reviewRepo.save(review);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepo.findAll();
    }

    @Override
    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepo.findReviewsByBookId(bookId);
    }
}
