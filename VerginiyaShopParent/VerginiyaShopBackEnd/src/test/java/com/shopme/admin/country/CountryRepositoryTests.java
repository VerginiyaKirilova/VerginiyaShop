package com.shopme.admin.country;

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
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.CountryRepository;
import com.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {

    @Autowired
    private CountryRepository repo;

    @BeforeEach
    public void setup() {
        // Инициализация на тестови данни
        repo.save(new Country("USA", "US"));
        repo.save(new Country("Canada", "CA"));
        // Други държави, които искате да тествате
    }

    @AfterEach
    public void cleanup() {
        repo.deleteAll(); // Изчиства всички записи от базата данни след всеки тест
    }

    @Test
    public void testCreateCountry() {
        Country country = repo.save(new Country("China", "CN"));
        assertThat(country).isNotNull();
        assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCountries() {
        List<Country> listCountries = repo.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);

        assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry() {
        Integer id = 1;
        String name = "Republic of India";

        Country country = repo.findById(id).get();
        country.setName(name);

        Country updatedCountry = repo.save(country);

        assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCountry() {
        Integer id = 3;
        Country country = repo.findById(id).get();
        assertThat(country).isNotNull();
    }

    @Test
    public void testDeleteCountry() {
        Integer id = 5;
        repo.deleteById(id);

        Optional<Country> findById = repo.findById(id);
        assertThat(findById.isEmpty());
    }
}