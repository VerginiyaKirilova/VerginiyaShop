package com.shopme.admin.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.RoleRepository;
import com.shopme.common.entity.Role;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository repo;
    private Integer createdRoleId;

    private final List<Integer> rolesToCleanUp = new ArrayList<>();

    @AfterEach
    public void cleanUp() {
        if (createdRoleId != null) {
            repo.deleteById(createdRoleId);
        }
    }

    @AfterEach
    public void cleanUpRoles() {
        rolesToCleanUp.forEach(repo::deleteById);
        rolesToCleanUp.clear();
    }

    @Test
    public void testCreateFirstRole() {
        String roleName = "Admin";
        String roleDescription = "manage everything";


        Optional<Role> existingRole = repo.findByName(roleName);

        if (existingRole.isPresent()) {

            assertThat(existingRole.get().getDescription()).isEqualTo(roleDescription);
        } else {

            Role roleAdmin = new Role(roleName, roleDescription);
            Role savedRole = repo.save(roleAdmin);
            createdRoleId = savedRole.getId();

            assertThat(savedRole.getId()).isGreaterThan(0);
        }
    }

    @Test
    public void testCreateRestRoles() {
        Role roleSalesperson = new Role("Salesperson_NEW", "manage product price, customers, shipping, orders and sales report");
        Role roleEditor = new Role("Editor_NEW", "manage categories, brands, products, articles and menus");
        Role roleShipper = new Role("Shipper_NEW", "view products, view orders and update order status");
        Role roleAssistant = new Role("Assistant_NEW", "manage questions and reviews");

        rolesToCleanUp.add(repo.save(roleSalesperson).getId());
        rolesToCleanUp.add(repo.save(roleEditor).getId());
        rolesToCleanUp.add(repo.save(roleShipper).getId());
        rolesToCleanUp.add(repo.save(roleAssistant).getId());
    }
}