package com.shopme.admin.setting.state;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Currency;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
    List<Currency> findAllByOrderByNameAsc();
}
