package com.shopme.admin.setting.state;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer>, JpaRepository<State, Integer> {

    public List<State> findByCountryOrderByNameAsc(Country country);
}