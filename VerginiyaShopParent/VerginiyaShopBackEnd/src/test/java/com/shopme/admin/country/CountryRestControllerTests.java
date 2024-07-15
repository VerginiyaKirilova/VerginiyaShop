package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.repository.CountryRepository;
import com.shopme.common.entity.Country;


@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTests {

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    CountryRepository repo;

    @Autowired
    public CountryRestControllerTests(MockMvc mockMvc, ObjectMapper objectMapper, CountryRepository repo) {
        super();
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.repo = repo;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String encodedPassword;

    @BeforeEach
    public void setup() {
        encodedPassword = passwordEncoder.encode("nam2020");
    }

    private static final String TEST_COUNTRY_NAME = "Germany_N";
    private static final String TEST_COUNTRY_CODE = "DE";
    private Integer testCountryId; // To store the ID of the created test country

    @BeforeEach
    public void setupCountryCreate() {
        // Check if the test country already exists and delete it if necessary
        Country existingCountry = repo.findByName(TEST_COUNTRY_NAME);
        if (existingCountry != null) {
            repo.delete(existingCountry);
        }
    }

    @AfterEach
    public void cleanupCountryCreate() {
        // Clean up the test country after the test
        if (testCountryId != null) {
            repo.deleteById(testCountryId);
        }
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testListCountries() throws Exception {
        String url = "/countries/list";

        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testCreateCountry() throws JsonProcessingException, Exception {
        String url = "/countries/save";

        Country country = new Country(TEST_COUNTRY_NAME, TEST_COUNTRY_CODE);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        testCountryId = Integer.parseInt(response);

        Country savedCountry = repo.findById(testCountryId).orElseThrow(() -> new RuntimeException("Country not found"));

        assertThat(savedCountry.getName()).isEqualTo(TEST_COUNTRY_NAME);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testUpdateCountry() throws JsonProcessingException, Exception {
        String url = "/countries/save";


        Country country = new Country(TEST_COUNTRY_NAME, TEST_COUNTRY_CODE);


        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer countryId = Integer.parseInt(response);


        testCountryId = countryId;


        Optional<Country> findById = repo.findById(countryId);
        assertThat(findById.isPresent()).isTrue();

        Country savedCountry = findById.get();
        assertThat(savedCountry.getName()).isEqualTo(TEST_COUNTRY_NAME);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testDeleteCountry() throws Exception {
        // Create a test country for deletion
        Country country = new Country();
        country.setName("TestCountry");
        country.setCode("TC");

        // Save the test country to get its ID
        Country savedCountry = repo.save(country);
        Integer countryId = savedCountry.getId();

        // Perform the DELETE request with CSRF token
        String url = "/countries/delete/" + countryId;
        mockMvc.perform(delete(url).with(csrf()))
                .andExpect(status().isOk());

        // Verify that the country is deleted from the repository
        Optional<Country> findById = repo.findById(countryId);
        assertThat(findById).isNotPresent();
    }
}