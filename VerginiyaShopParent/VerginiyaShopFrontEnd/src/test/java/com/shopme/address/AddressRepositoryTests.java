package com.shopme.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.shopme.common.entity.*;
import com.shopme.repository.CountryRepository;
import com.shopme.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.repository.AddressRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository repo;

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private CountryRepository countryRepo;

    @PersistenceContext
    private EntityManager entityManager;
    private Integer testAddressId;
    private Integer testCustomerId;
    private Customer testCustomer;
    private Country testCountry;

    @BeforeEach
    public void setUp() {
        testCountry = new Country();
        testCountry.setId(998);
        testCountry.setName("United States of America_NEW");
        testCountry.setCode("USA");
        testCountry = countryRepo.save(testCountry);

        testCustomer = new Customer();
        testCustomer.setEmail("test3@example.com");
        testCustomer.setPassword("password");
        testCustomer.setFirstName("Johne");
        testCustomer.setLastName("Dole");
        testCustomer.setAddressLine1("123 Main St");
        testCustomer.setAddressLine2("130 Main St");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setCity("New York");
        testCustomer.setState("New York");
        testCustomer.setPostalCode("10001");
        testCustomer.setEnabled(true);
        testCustomer.setAuthenticationType(AuthenticationType.DATABASE);
        testCustomer.setCreatedTime(new Date());
        testCustomer = customerRepo.save(testCustomer);

        Address address1 = new Address();
        address1.setCustomer(testCustomer);
        address1.setFirstName("Johne");
        address1.setLastName("Dole");
        address1.setPhoneNumber("1234567890");
        address1.setAddressLine1("123 Main St");
        address1.setAddressLine2("140 Main St");
        address1.setCity("New York");
        address1.setState("New York");
        address1.setCountry(testCountry);
        address1.setPostalCode("10001");
        repo.save(address1);

        Address address2 = new Address();
        address2.setCustomer(testCustomer);
        address2.setFirstName("Jane");
        address2.setLastName("Doe");
        address2.setPhoneNumber("0987654321");
        address2.setAddressLine1("578 Oak St");
        address2.setAddressLine2("456 Oak St");
        address2.setCity("New York");
        address2.setState("New York");
        address2.setCountry(testCountry);
        address2.setPostalCode("10002");
        repo.save(address2);

        entityManager.flush();
        testAddressId = address1.getId();
        testCustomerId = testCustomer.getId();
    }

    @AfterEach
    public void tearDown() {
        if (testCustomer != null) {
            List<Address> addresses = repo.findByCustomer(testCustomer);
            for (Address address : addresses) {
                repo.deleteById(address.getId());
            }
            customerRepo.deleteById(testCustomer.getId());
        }
        if (testCountry != null) {
            countryRepo.deleteById(testCountry.getId());
        }
    }

    @Test
    public void testAddNew() {
        Integer customerId = 1;
        Integer countryId = 231;// USA

        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        Country country = countryRepo.findById(countryId).orElseThrow(() -> new RuntimeException("Country not found"));

        Address newAddress = new Address();
        newAddress.setCustomer(customer);
        newAddress.setCountry(country);
        newAddress.setFirstName("Charles");
        newAddress.setLastName("Brugger");
        newAddress.setPhoneNumber("646-232-3902");
        newAddress.setAddressLine1("204 Morningview Lane");
        newAddress.setCity("New York");
        newAddress.setState("New York");
        newAddress.setPostalCode("10013");

        Address savedAddress = repo.save(newAddress);


        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isGreaterThan(0);


        repo.deleteById(savedAddress.getId());


        Optional<Address> deletedAddress = repo.findById(savedAddress.getId());
        assertThat(deletedAddress).isNotPresent();
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = testCustomer.getId();
        List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
        assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 27;
        Integer customerId = 19;

        Address address = repo.findByIdAndCustomer(addressId, customerId);

        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void testUpdate() {
        Integer addressId = 28;
        String phoneNumber = "646-232-3932";

        Address address = repo.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);

        Address updatedAddress = repo.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        repo.deleteByIdAndCustomer(testAddressId, testCustomerId);

        Address address = repo.findByIdAndCustomer(testAddressId, testCustomerId);
        assertThat(address).isNull();
    }

    @Test
    public void testSetDefault() {
        Integer addressId = 28;
        repo.setDefaultAddress(addressId);

        Address address = repo.findById(addressId).get();
        assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefaultAddresses() {
        Integer addressId = 8;
        Integer customerId = 5;
        repo.setNonDefaultForOthers(addressId, customerId);
    }

    @Test
    public void testGetDefault() {
        Integer customerId = 19;
        Address address = repo.findDefaultByCustomer(customerId);
        assertThat(address).isNotNull();
        System.out.println(address);
    }
}