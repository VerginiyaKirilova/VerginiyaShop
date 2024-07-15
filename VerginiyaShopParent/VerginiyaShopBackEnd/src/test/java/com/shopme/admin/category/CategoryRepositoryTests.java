package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import com.shopme.admin.repository.CategoryRepository;
import com.shopme.common.entity.Category;

import javax.transaction.Transactional;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
@Rollback(false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repo;

    @Test
    public void testCreateRootCategory() {
        String categoryName = "Home Appliances";

        Category existingCategory = repo.findByName(categoryName);

        if (existingCategory != null) {
            System.out.println("Category '" + categoryName + "' already exists in the database. Skipping creation.");
            assertThat(existingCategory).isNotNull();
            assertThat(existingCategory.getId()).isGreaterThan(0);
        } else {
            Category category = new Category(categoryName);
            try {
                Category savedCategory = repo.save(category);
                assertThat(savedCategory.getId()).isGreaterThan(0);
            } catch (DataIntegrityViolationException e) {
                fail("Exception thrown during category creation: " + e.getMessage());
            }
        }
    }

    @Test
    public void testCreateSubCategory() {
        int parentId = 7;
        String subCategoryName = "Samsung Galaxy";

        Category parentCategory = new Category(parentId);
        Category subCategory = new Category(subCategoryName, parentCategory);

        boolean subCategoryExists = false;
        for (Category category : repo.findAll()) {
            if (category.getName().equals(subCategoryName) && category.getParent().getId() == parentId) {
                subCategoryExists = true;
                break;
            }
        }

        if (subCategoryExists) {
            System.out.println("Sub-category '" + subCategoryName + "' already exists under parent category with ID " + parentId + ". Skipping creation.");
        } else {
            try {
                Category savedCategory = repo.save(subCategory);
                assertThat(savedCategory.getId()).isGreaterThan(0);
            } catch (DataIntegrityViolationException e) {
                fail("Exception thrown during sub-category creation: " + e.getMessage());
            }
        }
    }


    @Test
    public void testCreateMultiSubCategory() {
        Category parent = new Category(2);
        Category subCategory1 = new Category("CamerasNew", parent);
        Category subCategory2 = new Category("SmartphonesNew", parent);

        // Check if subCategory1 already exists
        boolean subCategory1Exists = repo.existsByNameAndParent(subCategory1.getName(), parent);
        if (!subCategory1Exists) {
            repo.save(subCategory1);
        } else {
            System.out.println("Sub-category '" + subCategory1.getName() + "' already exists under parent category.");
        }

        // Check if subCategory2 already exists
        boolean subCategory2Exists = repo.existsByNameAndParent(subCategory2.getName(), parent);
        if (!subCategory2Exists) {
            repo.save(subCategory2);
        } else {
            System.out.println("Sub-category '" + subCategory2.getName() + "' already exists under parent category.");
        }
    }

    @Test
    public void testGetCategory() {
        Category category = repo.findById(2).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();

        for (Category subCategory : children) {
            System.out.println(subCategory.getName());
        }

        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = repo.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }

    @Test
    public void testListRootCategories() {
        List<Category> rootCategories = repo.findRootCategories(Sort.by("name").ascending());
        rootCategories.forEach(cat -> System.out.println(cat.getName()));
    }

    @Test
    public void testFindByName() {
        String name = "Electronics"; // Computers1
        Category category = repo.findByName(name);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    public void testFindByAlias() {
        String alias = "Electronics";
        Category category = repo.findByAlias(alias);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(alias);
    }
}