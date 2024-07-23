package com.shopme.admin.shippingrate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.ShippingRateRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTests {
    @Autowired
    private ShippingRateRepository repo;
    @Autowired
    private TestEntityManager entityManager;
    private int testRateId;

    @Autowired
    public ShippingRateRepositoryTests(ShippingRateRepository repo, TestEntityManager entityManager) {
        super();
        this.repo = repo;
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void setUp() {
        ShippingRate rate = entityManager.find(ShippingRate.class, 26);
        if (rate == null) {
            rate = new ShippingRate();
            rate.setId(1);
            rate.setRate(5.00f);
            rate.setDays(5);
            rate.setState("InitialState");
            entityManager.persist(rate);
        } else {
            rate.setRate(5.00f);
            rate.setDays(5);
            rate.setState("InitialState");
            entityManager.merge(rate);
        }
        entityManager.flush();
    }

    @BeforeEach
    public void setUpDelete() {
        ShippingRate rate = new ShippingRate();
        rate.setRate(5.00f);
        rate.setDays(5);
        rate.setState("InitialState");


        rate = entityManager.merge(rate);
        entityManager.flush();


        testRateId = rate.getId();
    }


    @Test
    public void testCreateNew() {
        Country india = new Country(106);
        ShippingRate newRate = new ShippingRate();
        newRate.setCountry(india);
        newRate.setState("Maharashtra");
        newRate.setRate(8.25f);
        newRate.setDays(3);
        newRate.setCodSupported(true);

        ShippingRate savedRate = repo.save(newRate);
        assertThat(savedRate).isNotNull();
        assertThat(savedRate.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdate() {
        Integer rateId = 26;
        ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
        if (rate == null) {
            rate = new ShippingRate();
            rate.setId(rateId);
            rate.setRate(5.00f);
            rate.setDays(5);
            rate.setState("InitialState");
            entityManager.persist(rate);
        } else {
            rate.setRate(9.15f);
            rate.setDays(2);
            rate.setState("UpdatedState");
            entityManager.merge(rate);
        }
        entityManager.flush();
        ShippingRate updatedRate = repo.findById(rateId).orElse(null);
        assertThat(updatedRate).isNotNull();
        assertThat(updatedRate.getRate()).isEqualTo(9.15f);
        assertThat(updatedRate.getDays()).isEqualTo(2);
        assertThat(updatedRate.getState()).isEqualTo("UpdatedState");
    }

    @Test
    public void testFindAll() {
        List<ShippingRate> rates = (List<ShippingRate>) repo.findAll();
        assertThat(rates.size()).isGreaterThan(0);

        rates.forEach(System.out::println);
    }

    @Test
    public void testFindByCountryAndState() {
        Integer countryId = 106;
        String state = "Maharashtra";

        List<ShippingRate> rates = repo.findAllByCountryAndState(countryId, state);
        assertThat(rates).isNotEmpty();


        ShippingRate rate = rates.get(0);


        assertThat(rate.getCountry().getId()).isEqualTo(countryId);
        assertThat(rate.getState()).isEqualTo(state);


        System.out.println(rate);
    }


    @Test
    public void testUpdateCODSupport() {
        Integer rateId = 27;
        repo.updateCODSupport(rateId, false);

        ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
        assertThat(rate.isCodSupported()).isFalse();

    }

    @Test
    public void testDelete() {
        ShippingRate rate = entityManager.find(ShippingRate.class, testRateId);

        entityManager.remove(rate);
        entityManager.flush();

        ShippingRate deletedRate = entityManager.find(ShippingRate.class, testRateId);

        assertNull(deletedRate);
    }
}