package com.shopme.admin.article;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import com.shopme.admin.repository.CustomerRepository;
import com.shopme.admin.repository.UserRepository;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.ArticleRepository;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.article.ArticleType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ArticleRepositoryTests {

    @Autowired
    private ArticleRepository repo;
    @Autowired
    private UserRepository userRepo;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setFirstName("Verginiya");
        user.setLastName("Kirilova");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        userRepo.save(user);

        Article article = new Article();
        article.setTitle("Test Article");
        article.setContent("Content of the test article");
        article.setAlias("test-article");
        article.setType(ArticleType.MENU_BOUND);
        article.setPublished(true);
        article.setUpdatedTime(new Date());
        article.setUser(user);
        repo.save(article);
    }

    @Test
    public void testListMenuBoundArticles() {
        List<Article> listArticles = repo.findByTypeOrderByTitle(ArticleType.MENU_BOUND);
        assertThat(listArticles).isNotEmpty();

        listArticles.forEach(System.out::println);
    }
}