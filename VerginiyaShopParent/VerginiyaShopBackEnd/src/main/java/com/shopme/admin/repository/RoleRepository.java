package com.shopme.admin.repository;

import com.shopme.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer>, JpaRepository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.name = ?1")
    Optional<Role> findByName(String name);
}
