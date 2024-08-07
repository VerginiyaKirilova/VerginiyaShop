
package com.shopme.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import com.shopme.common.entity.product.Product;
import com.shopme.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;
import com.shopme.repository.ReviewVoteRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewVoteRepositoryTests {

    @Autowired
    private ReviewVoteRepository repo;
    @Autowired
    private ReviewRepository reviewRepo;
    Integer customerId;
    Integer productId;
    Integer reviewId;
    Integer reviewVoteId;

    @BeforeEach
    public void setUp() {
        customerId = 19;
        productId = 3;

        Customer customer = new Customer(customerId);
        Product product = new Product(productId);

        Review review = new Review();
        review.setHeadline("Perfect for my needs. Loving it!");
        review.setComment("Nice to have: wireless remote, iOS app, GPS...");
        review.setReviewTime(new Date());
        review.setRating(5);
        review.setCustomer(customer);
        review.setProduct(product);
        review.setVotes(1);

        Review savedReview = reviewRepo.save(review);
        reviewId = savedReview.getId();

        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVotes(1);
        reviewVote.setCustomer(customer);
        reviewVote.setReview(savedReview);

        ReviewVote savedReviewVote1 = repo.save(reviewVote);
        reviewVoteId = savedReviewVote1.getId();

        repo.save(reviewVote);
    }

    @AfterEach
    void tearDown() {
        if (reviewVoteId != null) {
            repo.deleteById(reviewVoteId);
        }
        if (reviewId != null) {
            reviewRepo.deleteById(reviewId);
        }
    }

    @Test
    public void testSaveVote() {
        Integer customerId = 1;
        Integer reviewId = 42;

        ReviewVote vote = new ReviewVote();
        vote.setCustomer(new Customer(customerId));
        vote.setReview(new Review(reviewId));
        vote.voteUp();

        ReviewVote savedVote = repo.save(vote);
        assertThat(savedVote.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByReviewAndCustomer() {

        ReviewVote vote = repo.findByReviewAndCustomer(reviewId, customerId);
        assertThat(vote).isNotNull();

        System.out.println(vote);
    }

    @Test
    public void testFindByProductAndCustomer() {
        Integer customerId = 1;
        Integer productId = 3;

        List<ReviewVote> listVotes = repo.findByProductAndCustomer(productId, customerId);
        assertThat(listVotes.size()).isGreaterThan(0);

        listVotes.forEach(System.out::println);
    }
}
