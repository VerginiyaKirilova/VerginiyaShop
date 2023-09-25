package com.shopme.admin.setting.country;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Integer>, JpaRepository<Country, Integer> {
    public List<Country> findAllByOrderByNameAsc();
}
