package com.shopme.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.shopme.common.entity.*;
import com.shopme.common.entity.product.Product;
import com.shopme.repository.CustomerRepository;
import com.shopme.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.repository.ReviewRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class ReviewVoteRestControllerTests {
    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private MockMvc mockMvc;
    @PersistenceContext
    private EntityManager entityManager;

    private ObjectMapper objectMapper;
    Integer reviewId;

    @Autowired
    public ReviewVoteRestControllerTests(ReviewRepository reviewRepo, MockMvc mockMvc, ObjectMapper objectMapper) {
        super();
        this.reviewRepo = reviewRepo;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        removeExistingVote(reviewId, "vegitoo@abv.bg");
    }

    @AfterEach
    public void tearDown() {
        removeExistingVote(reviewId, "vegitoo@abv.bg");
    }

    @Test
    public void testVoteNotLogin() throws Exception {
        String requestURL = "/vote_review/1/up";

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResultDTO voteResult = objectMapper.readValue(json, VoteResultDTO.class);

        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("You must login");
    }

    @Test
    @WithMockUser(username = "vegitoo@abv.bg", password = "$2a$10$ReEWvZsJ2jdNBo7QPwG/De2uDbYrzD9SQOIlNCAojMlTAOpFNnk0K")
    public void testVoteNonExistReview() throws Exception {
        String requestURL = "/vote_review/123/up";

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResultDTO voteResult = objectMapper.readValue(json, VoteResultDTO.class);

        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("no longer exists");
    }


    @Test
    @WithMockUser(username = "vegitoo@abv.bg", password = "$2a$10$ReEWvZsJ2jdNBo7QPwG/De2uDbYrzD9SQOIlNCAojMlTAOpFNnk0K")
    public void testVoteUp() throws Exception {
        reviewId = 39;
        String requestURL = "/vote_review/" + reviewId + "/up";

        Review review = reviewRepo.findById(reviewId).orElseThrow();
        int voteCountBefore = review.getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResultDTO voteResult = objectMapper.readValue(json, VoteResultDTO.class);


        if (voteCountBefore == review.getVotes()) {
            assertTrue(voteResult.isSuccessful());
            assertThat(voteResult.getMessage()).contains("successfully voted up");
        } else {
            assertTrue(voteResult.isSuccessful());
            assertThat(voteResult.getMessage()).contains("You have unvoted up that review.");
        }

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore + 1, voteCountAfter);
    }

    private void removeExistingVote(Integer reviewId, String userEmail) {
        if (reviewId == null || userEmail == null) {
            return;
        }

        Review review = reviewRepo.findById(reviewId).orElse(null);
        if (review != null) {
            review.setVotes(0);
            reviewRepo.save(review);
        }
    }

    @Test
    @WithMockUser(username = "vegitoo@abv.bg", password = "$2a$10$ReEWvZsJ2jdNBo7QPwG/De2uDbYrzD9SQOIlNCAojMlTAOpFNnk0K")
    public void testUndoVoteUp() throws Exception {
        Integer productId = 1;
        Product existingProduct = productRepo.findById(productId).get();

        Review temporaryReview = new Review();
        temporaryReview.setComment("This is a temporary review for testing.");
        temporaryReview.setHeadline("Temporary Review Headline");
        temporaryReview.setRating(4);
        temporaryReview.setReviewTime(new Date());
        temporaryReview.setVotes(1);

        temporaryReview.setCustomer(customerRepo.findByEmail("vegitoo@abv.bg"));
        temporaryReview.setProduct(existingProduct);


        temporaryReview = reviewRepo.save(temporaryReview);
        Integer reviewId = temporaryReview.getId();


        String requestURL = "/vote_review/" + reviewId + "/up";
        mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk());


        Review reviewAfterVote = reviewRepo.findById(reviewId).get();
        int voteCountBefore = reviewAfterVote.getVotes();


        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResultDTO voteResult = objectMapper.readValue(json, VoteResultDTO.class);


        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("unvoted up");

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore - 1, voteCountAfter);

        reviewRepo.delete(temporaryReview);

    }

}