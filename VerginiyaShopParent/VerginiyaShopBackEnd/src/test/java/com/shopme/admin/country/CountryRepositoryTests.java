package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.CountryRepository;
import com.shopme.common.entity.Country;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {

    @Autowired
    private CountryRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    private Integer createdCountryId;

    @Test
    public void testCreateCountry() {
        String countryName = "China_New";
        String countryCode = "CN";

        // Check if the country already exists
        boolean countryExists = repo.existsByName(countryName);
        if (countryExists) {
            System.out.println("Country '" + countryName + "' already exists.");
        } else {
            try {
                Country country = repo.save(new Country(countryName, countryCode));
                assertThat(country).isNotNull();
                assertThat(country.getId()).isGreaterThan(0);
            } catch (DataIntegrityViolationException e) {
                fail("Exception thrown during country creation: " + e.getMessage());
            }
        }
    }

    @Test
    public void testListCountries() {
        List<Country> listCountries = repo.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);

        assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry() {
        String uniqueCountryName = "Country " + UUID.randomUUID();
        String uniqueCountryCode = "C" + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
        Country newCountry = new Country(uniqueCountryName, uniqueCountryCode);
        Country savedCountry = repo.save(newCountry);
        createdCountryId = savedCountry.getId();

        String updatedName = "Republic of India";

        savedCountry.setName(updatedName);
        Country updatedCountry = repo.save(savedCountry);

        assertThat(updatedCountry.getName()).isEqualTo(updatedName);

        // Cleanup
        repo.deleteById(createdCountryId);
    }

    @Test
    public void testGetCountry() {
        Integer id = 49;
        Country country = repo.findById(id).get();
        assertThat(country).isNotNull();
    }

    @Test
    public void testDeleteCountry() {
        String uniqueCountryName = "Country " + UUID.randomUUID();
        String uniqueCountryCode = "C" + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
        Country newCountry = new Country(uniqueCountryName, uniqueCountryCode);
        Country savedCountry = repo.save(newCountry);
        Integer createdCountryId = savedCountry.getId();

        repo.deleteById(createdCountryId);


        Optional<Country> findById = repo.findById(createdCountryId);
        assertThat(findById.isEmpty()).isTrue();
    }
}