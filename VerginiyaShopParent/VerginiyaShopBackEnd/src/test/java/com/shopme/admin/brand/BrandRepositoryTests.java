package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.shopme.admin.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.BrandRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository repo;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateBrand1() {
        Category laptops = new Category(6);
        Brand acer = new Brand("Acer");
        acer.getCategories().add(laptops);

        Brand savedBrand = repo.save(acer);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    private Brand findBrandByName(String name) {
        return repo.findByName(name);
    }

    @Test
    public void testCreateBrand2() {
        Category cellphones = new Category(6);
        Category tablets = new Category(7);

        Brand apple = new Brand("ZTE");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);

        Brand existingBrand = findBrandByName("ZTE");

        if (existingBrand != null) {
            System.out.println("Brand 'ZTE' already exists in the database. Skipping creation.");
            assertThat(existingBrand).isNotNull();
            assertThat(existingBrand.getId()).isGreaterThan(0);
        } else {
            try {
                Brand savedBrand = repo.save(apple);
                assertThat(savedBrand).isNotNull();
                assertThat(savedBrand.getId()).isGreaterThan(0);
            } catch (DataIntegrityViolationException e) {
                fail("Exception thrown during brand creation: " + e.getMessage());
            }
        }
    }

    @Test
    public void testCreateBrand3() {
        Brand existingBrand = repo.findByName("Nokia");
        if (existingBrand != null) {
            fail("Brand with name 'Samsung' already exists in the database.");
        }

        Brand samsung = new Brand("Nokia");

        samsung.getCategories().add(new Category(108));    // category memory
        samsung.getCategories().add(new Category(109));    // category internal hard drive
        Brand savedBrand = repo.save(samsung);
        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindAll() {
        Iterable<Brand> brands = repo.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    public void testGetById() {
        Brand brand = repo.findById(153).get();

        assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    public void testUpdateName() {
        String newName = "Samsung Electronics";
        Brand samsung = repo.findById(3).get();
        samsung.setName(newName);

        Brand savedBrand = repo.save(samsung);
        assertThat(savedBrand.getName()).isEqualTo(newName);
    }

    @Test
    public void testDelete() {
        Integer id = 2;
        if (repo.existsById(id)) {
            repo.deleteById(id);
            Optional<Brand> result = repo.findById(id);
            assertThat(result.isEmpty()).isTrue();
        } else {
            System.out.println("Brand with ID " + id + " does not exist");
        }
    }

}