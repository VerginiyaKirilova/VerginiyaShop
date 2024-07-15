package com.shopme.admin.repository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Integer>, JpaRepository<Country, Integer> {
    boolean existsByName(String name);

    public List<Country> findAllByOrderByNameAsc();

    @Query("SELECT c FROM Country c WHERE c.name = :name")
    public Country findByName(String name);
}
