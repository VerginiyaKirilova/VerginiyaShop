package com.shopme.admin.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.SectionRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.section.ArticleSection;
import com.shopme.common.entity.section.BrandSection;
import com.shopme.common.entity.section.CategorySection;
import com.shopme.common.entity.section.ProductSection;
import com.shopme.common.entity.section.Section;
import com.shopme.common.entity.section.SectionType;

import javax.transaction.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SectionRepositoryTests {

    @Autowired
    private SectionRepository repo;
    private Integer savedSectionId;
    private Integer additionalSavedSectionId;
    private Integer testSectionId;
    private static int nextOrder = 1;

    @BeforeEach
    public void setUp() {
        Section section = new Section();
        section.setHeading("Announcement_NEW");
        section.setDescription("Shop Announcement in Summer 2022: The great sales season is coming...");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);

        Section savedSection = repo.save(section);
        savedSectionId = savedSection.getId();


        Section additionalSection = new Section();
        additionalSection.setHeading("Shopping by Categories new");
        additionalSection.setDescription("Check out all categories...");
        additionalSection.setType(SectionType.ALL_CATEGORIES);
        additionalSection.setSectionOrder(2);

        Section additionalSavedSection = repo.save(additionalSection);
        additionalSavedSectionId = additionalSavedSection.getId();
    }

    @AfterEach
    public void cleanUp() {

        if (savedSectionId != null) {
            if (repo.existsById(savedSectionId)) {
                repo.deleteById(savedSectionId);
            }
            savedSectionId = null;
        }

        if (additionalSavedSectionId != null) {
            if (repo.existsById(additionalSavedSectionId)) {
                repo.deleteById(additionalSavedSectionId);
            }
            additionalSavedSectionId = null;
        }

        if (testSectionId != null) {
            if (repo.existsById(testSectionId)) {
                repo.deleteById(testSectionId);
            }
            testSectionId = null;
        }
    }

    @Test
    public void testAddTextSection() {
        Section section = new Section();
        section.setHeading("Another Announcement");
        section.setDescription("Another Shop Announcement");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(2);

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);

        // Clean up the newly added section
        repo.deleteById(savedSection.getId());
    }

    @Test
    public void testListSectionsNotSorted() {
        List<Section> sections = repo.findAll();
        assertThat(sections).isNotEmpty();

        sections.forEach(System.out::println);
    }

    @Test
    public void testListSectionsSorted() {
        List<Section> sections = repo.findAllSectionsSortedByOrder();

        assertThat(sections).isNotEmpty();
        sections.forEach(System.out::println);
    }

    @Test
    public void testAddAllCategoriesSection() {

        Section section = new Section();
        section.setHeading("Unique Shopping by Categories");
        section.setDescription("Check out all categories...");
        section.setType(SectionType.ALL_CATEGORIES);
        section.setSectionOrder(2);

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);

        // Clean up the newly added section
        repo.deleteById(savedSection.getId());
    }

    @Test
    public void testDisableSection() {
        Integer sectionId = 2;
        repo.updateEnabledStatus(sectionId, false);
        Section section = repo.findById(sectionId).get();

        assertThat(section.isEnabled()).isFalse();
    }

    @Test
    public void testEnableSection() {
        Integer sectionId = 2;
        repo.updateEnabledStatus(sectionId, true);
        Section section = repo.findById(sectionId).get();

        assertThat(section.isEnabled()).isTrue();
    }

    @Test
    public void testDeleteSection() {
        Section sectionToDelete = new Section();
        sectionToDelete.setHeading("Test Section");
        sectionToDelete.setDescription("This section will be deleted");
        sectionToDelete.setType(SectionType.TEXT);
        sectionToDelete.setSectionOrder(3);

        Section savedSectionToDelete = repo.save(sectionToDelete);
        Integer sectionToDeleteId = savedSectionToDelete.getId();

        repo.deleteById(sectionToDeleteId);
        Optional<Section> findById = repo.findById(sectionToDeleteId);


        assertThat(findById).isNotPresent();
    }

    @Test
    public void testAddBrandSection() {
        Section section = new Section();
        section.setHeading("Featured Brands");
        section.setDescription("Recommended brands for shopping...");
        section.setType(SectionType.BRAND);
        section.setSectionOrder(5);

        for (int i = 1; i <= 3; i++) {
            BrandSection brandSection = new BrandSection();
            Brand brand = new Brand();
            brand.setId(i);

            brandSection.setBrandOrder(i);
            brandSection.setBrand(brand);

            section.addBrandSection(brandSection);
        }

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);

        // Clean up the newly added section
        repo.deleteById(savedSection.getId());
    }

    @Test
    public void testAddArticleSection() {
        Section section = new Section();
        section.setHeading("Unique Shopping Tips Test");
        section.setDescription("Read these articles before shopping...");
        section.setType(SectionType.ARTICLE);
        section.setSectionOrder(6);


        for (int i = 1; i <= 3; i++) {
            ArticleSection articleSection = new ArticleSection();
            articleSection.setArticle(new Article(4 + i));
            articleSection.setArticleOrder(i);

            section.addArticleSection(articleSection);
        }


        Section savedSection = repo.save(section);


        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);


        testSectionId = savedSection.getId();

    }

    @Test
    public void testAddCategorySection() {
        Section section = new Section();
        section.setHeading("Featured Categories ");
        section.setDescription("Check out these featured categories...");
        section.setType(SectionType.CATEGORY);
        section.setSectionOrder(3);

        for (int i = 1; i <= 3; i++) {
            CategorySection categorySection = new CategorySection();


            Category category = new Category(i + 1);
            categorySection.setCategory(category);
            categorySection.setCategoryOrder(i);

            section.addCategorySection(categorySection);
        }

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);


        testSectionId = savedSection.getId();
    }

    @Test
    public void testAddProductSection() {
        Section section = new Section();
        section.setHeading("Featured Products_" + nextOrder);
        section.setDescription("Check out these best-selling items...");
        section.setType(SectionType.PRODUCT);
        section.setSectionOrder(nextOrder++);

        for (int i = 1; i <= 3; i++) {
            ProductSection productSection = new ProductSection();
            Product product = new Product(i);

            productSection.setProduct(product);
            productSection.setProductOrder(i);

            section.addProductSection(productSection);
        }

        Section savedSection = repo.save(section);
        testSectionId = savedSection.getId();

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);
    }
}