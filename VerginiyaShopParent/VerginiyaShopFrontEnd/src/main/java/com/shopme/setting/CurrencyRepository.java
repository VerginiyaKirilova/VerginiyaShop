package com.shopme.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>, JpaRepository<Currency, Integer> {

}
