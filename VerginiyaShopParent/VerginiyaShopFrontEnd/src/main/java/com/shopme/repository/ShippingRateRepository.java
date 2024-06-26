package com.shopme.repository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer>, JpaRepository<ShippingRate, Integer> {

    public ShippingRate findByCountryAndState(Country country, String state);
}