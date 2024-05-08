package com.shopme.admin.setting;

import com.shopme.common.entity.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Currency;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>, JpaRepository<Currency, Integer> {
    List<Currency> findAllByOrderByNameAsc();
}
