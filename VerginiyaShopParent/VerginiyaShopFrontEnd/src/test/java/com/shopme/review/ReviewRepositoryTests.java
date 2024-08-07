
package com.shopme.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ReviewVote;
import com.shopme.repository.ReviewVoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.repository.ReviewRepository;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository repo;
    @Autowired
    private ReviewVoteRepository reviewVoteRepo;

    Integer customerId;
    Integer productId;
    Integer reviewId;
    Integer reviewVoteId;

    @BeforeEach
    void setUp() {
        customerId = 19;
        productId = 3;

        Product product = new Product(productId);
        Customer customer = new Customer(customerId);

        Review review = new Review();
        review.setHeadline("Perfect for my needs. Loving it!");
        review.setComment("Nice to have: wireless remote, iOS app, GPS...");
        review.setReviewTime(new Date());
        review.setRating(5);
        review.setCustomer(customer);
        review.setProduct(product);
        review.setVotes(1);

        Review savedReview = repo.save(review);
        reviewId = savedReview.getId();

        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVotes(1);
        reviewVote.setCustomer(customer);
        reviewVote.setReview(savedReview);
        ReviewVote savedReviewVote1 = reviewVoteRepo.save(reviewVote);
        reviewVoteId = savedReviewVote1.getId();


        assertThat(reviewVoteId).isNotNull();

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isGreaterThan(0);
    }

    @AfterEach
    void tearDown() {
        if (reviewVoteId != null) {
            reviewVoteRepo.deleteById(reviewVoteId);
        }
        if (reviewId != null) {
            repo.deleteById(reviewId);
        }
    }

    @Test
    public void testFindByCustomerNoKeyword() {
        Integer customerId = 1;
        Pageable pageable = PageRequest.of(1, 5);

        Page<Review> page = repo.findByCustomer(customerId, pageable);
        long totalElements = page.getTotalElements();

        assertThat(totalElements).isGreaterThan(1);
    }

    @Test
    public void testFindByCustomerWithKeyword() {
        Integer customerId = 1;
        String keyword = "iOS";
        Pageable pageable = PageRequest.of(1, 5);

        Page<Review> page = repo.findByCustomer(customerId, keyword, pageable);
        long totalElements = page.getTotalElements();

        assertThat(totalElements).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomerAndId() {
        Integer customerId = 1;
        Integer reviewId = 39;

        Review review = repo.findByCustomerAndId(customerId, reviewId);
        assertThat(review).isNotNull();
    }

    @Test
    public void testFindByProduct() {
        Product product = new Product(2);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Review> page = repo.findByProduct(product, pageable);

        assertThat(page.getTotalElements()).isGreaterThan(1);

        List<Review> content = page.getContent();
        content.forEach(System.out::println);
    }

    @Test
    public void testCountByCustomerAndProduct() {
        Long count = repo.countByCustomerAndProduct(customerId, productId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    public void testUpdateVoteCount() {
        Integer reviewId = 93;
        repo.updateVoteCount(reviewId);
        Review review = repo.findById(reviewId).get();

        assertThat(review.getVotes()).isEqualTo(2);
    }

    @Test
    public void testGetVoteCount() {
        Integer reviewId = 93;
        Integer voteCount = repo.getVoteCount(reviewId);

        assertThat(voteCount).isEqualTo(2);
    }
}
