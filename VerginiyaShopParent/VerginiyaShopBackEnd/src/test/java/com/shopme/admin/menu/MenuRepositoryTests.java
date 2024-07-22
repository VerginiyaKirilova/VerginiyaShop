package com.shopme.admin.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.MenuRepository;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class MenuRepositoryTests {

    @Autowired
    private MenuRepository repo;
    @Autowired
    private TestEntityManager entityManager;
    @PersistenceContext
    private EntityManager entityManagers;
    private static Integer createdMenuId;

    @BeforeEach
    public void setup() {
        if (createdMenuId != null) {
            repo.deleteById(createdMenuId);
            createdMenuId = null;
        }
    }

    @BeforeEach
    public void setupCount() {
        entityManagers.createQuery("DELETE FROM Menu m WHERE m.type = :type")
                .setParameter("type", MenuType.FOOTER)
                .executeUpdate();
    }

    @AfterEach
    public void cleanup() {
        if (createdMenuId != null) {
            repo.deleteById(createdMenuId);
            createdMenuId = null;
        }
    }

    @Test
    public void testCreateHeaderMenu() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setTitle("About Verginiyashop " + UUID.randomUUID().toString().substring(0, 5));
        menu.setAlias("about-" + UUID.randomUUID().toString().substring(0, 5));
        menu.setEnabled(true);
        menu.setPosition(1);

        Article article = new Article();
        article.setTitle("Sample Article " + UUID.randomUUID().toString().substring(0, 5));
        article.setContent("This is a sample article content.");
        article.setAlias("sample-article-" + UUID.randomUUID().toString().substring(0, 5));
        entityManager.persist(article);
        menu.setArticle(article);

        Menu savedMenu = repo.save(menu);
        createdMenuId = savedMenu.getId();

        assertTrue(savedMenu.getId() > 0);
    }

    @Test
    public void testCreateFooterMenu() {
        Menu menu = new Menu();
        menu.setType(MenuType.FOOTER);
        menu.setTitle("Shipping " + UUID.randomUUID().toString().substring(0, 5));
        menu.setAlias("shipping-" + UUID.randomUUID().toString().substring(0, 5));
        menu.setEnabled(false);
        menu.setPosition(2);


        Article article = new Article();
        article.setTitle("Sample Article " + UUID.randomUUID().toString().substring(0, 5));
        article.setContent("This is a sample article content.");
        article.setAlias("sample-article-" + UUID.randomUUID().toString().substring(0, 5));
        entityManager.persist(article);
        menu.setArticle(article);

        Menu savedMenu = repo.save(menu);
        createdMenuId = savedMenu.getId();

        assertTrue(savedMenu.getId() > 0);
    }

    @Test
    public void testListMenuByTypeThenByPosition() {
        List<Menu> listMenuItems = repo.findAllByOrderByTypeAscPositionAsc();
        assertThat(listMenuItems.size()).isGreaterThan(0);

        listMenuItems.forEach(System.out::println);
    }

    @Test
    public void testCountHeaderMenus() {
        Long numberOfFooterMenus = repo.countByType(MenuType.HEADER);
        assertEquals(1, numberOfFooterMenus);
    }

    @Transactional
    @Test
    public void testCountFooterMenus() {
        Menu footerMenu = new Menu();
        footerMenu.setType(MenuType.FOOTER);
        footerMenu.setTitle("Shipping");
        footerMenu.setAlias("shipping");
        footerMenu.setEnabled(true);
        footerMenu.setPosition(2);

        Article article = new Article();
        article.setTitle("Sample Article");
        article.setContent("This is a sample article content.");
        article.setAlias("sample-article");

        entityManager.persist(article);
        footerMenu.setArticle(article);

        repo.save(footerMenu);


        Long numberOfFooterMenus = repo.countByType(MenuType.FOOTER);
        assertEquals(1, numberOfFooterMenus);
    }

    @Test
    public void testDisableMenuItem() {
        Integer menuId = 68;
        repo.updateEnabledStatus(menuId, false);
        Menu updatedMenu = repo.findById(menuId).get();

        assertFalse(updatedMenu.isEnabled());
    }

    @Test
    public void testEnableMenuItem() {
        Integer menuId = 68;
        repo.updateEnabledStatus(menuId, true);
        Menu updatedMenu = repo.findById(menuId).get();

        assertTrue(updatedMenu.isEnabled());
    }

    @Test
    public void testListHeaderMenuItems() {
        List<Menu> listHeaderMenuItems = repo.findByTypeOrderByPositionAsc(MenuType.HEADER);
        assertThat(listHeaderMenuItems).isNotEmpty();

        listHeaderMenuItems.forEach(System.out::println);
    }

    @Transactional
    @Test
    public void testListFooterMenuItems() {
        // Create a menu item and save it
        Menu footerMenu = new Menu();
        footerMenu.setType(MenuType.FOOTER);
        footerMenu.setTitle("Shipping");
        footerMenu.setAlias("shipping");
        footerMenu.setEnabled(true);
        footerMenu.setPosition(2);

        repo.save(footerMenu);
        createdMenuId = footerMenu.getId();  // Store the created menu id

        // Now retrieve and validate the menu item
        Menu retrievedMenu = repo.findById(createdMenuId).orElse(null);
        assertThat(retrievedMenu).isNotNull();
        assertEquals("Shipping", retrievedMenu.getTitle());

    }

}