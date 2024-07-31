package com.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.repository.CustomerRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository repo;

    @Autowired
    private TestEntityManager entityManager;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        Integer countryId = 234; // USA
        Country country = entityManager.find(Country.class, countryId);

        customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Deivid");
        customer.setLastName("Fountaines");
        customer.setPassword("password123");
        customer.setEmail("deivid.s.fountaine@gmail.com");
        customer.setPhoneNumber("312-462-7518");
        customer.setAddressLine1("1927  West Drive");
        customer.setCity("Sacramento");
        customer.setState("California");
        customer.setPostalCode("95867");
        customer.setVerificationCode("code_12345");
        customer.setAuthenticationType(AuthenticationType.DATABASE);
        customer.setCreatedTime(new Date());
        customer.setEnabled(true);

        repo.save(customer);
    }

    @AfterEach
    public void tearDown() {
        if (customer != null && customer.getId() != null) {
            repo.deleteById(customer.getId());
        }
    }

    @Test
    public void testCreateCustomer1() {

        Customer retrievedCustomer = repo.findById(customer.getId()).orElse(null);


        assertThat(retrievedCustomer).isNotNull();
        assertThat(retrievedCustomer.getFirstName()).isEqualTo("Deivid");
        assertThat(retrievedCustomer.getEmail()).isEqualTo("deivid.s.fountaine@gmail.com");
    }

    @Test
    public void testCreateCustomer2() {

        Customer retrievedCustomer = repo.findById(customer.getId()).orElse(null);


        assertThat(retrievedCustomer).isNotNull();
        assertThat(retrievedCustomer.getFirstName()).isEqualTo("Deivid");
        assertThat(retrievedCustomer.getEmail()).isEqualTo("deivid.s.fountaine@gmail.com");
    }

    @Test
    public void testListCustomers() {
        Iterable<Customer> customers = repo.findAll();
        customers.forEach(System.out::println);

        assertThat(customers).hasSizeGreaterThan(1);
    }

    @Test
    public void testUpdateCustomer() {
        Integer customerId = customer.getId();
        String lastName = "Stanfield";

        Customer customer = repo.findById(customerId).get();
        customer.setLastName(lastName);
        customer.setEnabled(true);

        Customer updatedCustomer = repo.save(customer);
        assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void testGetCustomer() {
        Integer customerId = 1;
        Optional<Customer> findById = repo.findById(customerId);

        assertThat(findById).isPresent();

        Customer customer = findById.get();
        System.out.println(customer);
    }

    @Test
    public void testDeleteCustomer() {
        Integer countryId = 120; // India
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Sonya");
        customer.setLastName("Robins");
        customer.setPassword("password456");
        customer.setEmail("sonya.lad2020@gmail.com");
        customer.setPhoneNumber("02224928052");
        customer.setAddressLine1("173 , A-, Shah & Nahar Indl.estate, Sunmill Road");
        customer.setAddressLine2("Dhanraj Mill Compound, Lower Parel (west)");
        customer.setCity("Mumbai");
        customer.setState("Maharashtra");
        customer.setPostalCode("400013");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = repo.save(customer);
        Integer customerId = customer.getId();

        repo.deleteById(customerId);

        Optional<Customer> findById = repo.findById(customerId);
        assertThat(findById).isNotPresent();
    }

    @Test
    public void testFindByEmail() {
        String email = "deivid.s.fountaine@gmail.com";
        Customer customer = repo.findByEmail(email);

        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    public void testFindByVerificationCode() {
        String code = "code_12345";
        Customer customer = repo.findByVerificationCode(code);

        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    public void testEnableCustomer() {
        Integer customerId = customer.getId();
        repo.enable(customerId);

        Customer customer = repo.findById(customerId).get();
        assertThat(customer.isEnabled()).isTrue();
    }

    @Test
    public void testUpdateAuthenticationType() {
        Integer id = customer.getId();
        repo.updateAuthenticationType(id, AuthenticationType.DATABASE);

        Customer customer = repo.findById(id).get();

        assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
    }
}