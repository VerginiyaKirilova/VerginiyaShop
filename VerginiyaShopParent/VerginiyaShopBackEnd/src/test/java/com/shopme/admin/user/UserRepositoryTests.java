package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repository.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    private UserRepository repo;
    private TestEntityManager entityManager;

    private static final String BASE_PASSWORD = "password";
    private static Integer userId1;
    private static Integer userId2;

    @Autowired
    public UserRepositoryTests(UserRepository repo, TestEntityManager entityManager) {
        super();
        this.repo = repo;
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void setup() {
        // Setup any necessary data before each test
        userId1 = null;
        userId2 = null;
    }

    @AfterEach
    public void cleanup() {
        // Cleanup any data created during the tests
        if (userId1 != null) {
            repo.deleteById(userId1);
            userId1 = null;
        }
        if (userId2 != null) {
            repo.deleteById(userId2);
            userId2 = null;
        }
    }

    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);

        // Generate unique email for the test
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";

        User userWithOneRole = new User(uniqueEmail, BASE_PASSWORD, "Simo", "Sotirov");
        userWithOneRole.addRole(roleAdmin);

        User savedUser = repo.save(userWithOneRole);
        userId1 = savedUser.getId();

        assertThat(userId1).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRoles() {
        Role roleAdmin = entityManager.find(Role.class, 3);
        Role roleUser = entityManager.find(Role.class, 5);

        // Generate unique email for the test
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";

        User userWithTwoRoles = new User(uniqueEmail, BASE_PASSWORD, "Daniel", "Kirilov");
        userWithTwoRoles.addRole(roleAdmin);
        userWithTwoRoles.addRole(roleUser);

        User savedUser = repo.save(userWithTwoRoles);
        userId2 = savedUser.getId();

        assertThat(userId2).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = repo.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";
        User testUser = new User(uniqueEmail, BASE_PASSWORD, "John", "Doe");
        User savedUser = repo.save(testUser);
        Integer userId = savedUser.getId();

        User userById = repo.findById(userId).get();
        assertThat(userById).isNotNull();

        repo.deleteById(userId); // Cleanup
    }

    @Test
    public void testUpdateUserDetails() {
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";
        User testUser = new User(uniqueEmail, BASE_PASSWORD, "John", "Doe");
        User savedUser = repo.save(testUser);
        Integer userId = savedUser.getId();

        User userUpdateUserDetails = repo.findById(userId).get();
        userUpdateUserDetails.setEnabled(true);
        userUpdateUserDetails.setEmail("updated_" + uniqueEmail);

        repo.save(userUpdateUserDetails);

        User updatedUser = repo.findById(userId).get();
        assertThat(updatedUser.isEnabled()).isTrue();
        assertThat(updatedUser.getEmail()).isEqualTo("updated_" + uniqueEmail);

        repo.deleteById(userId); // Cleanup
    }

    @Test
    public void testUpdateUserRoles() {
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";
        User testUser = new User(uniqueEmail, BASE_PASSWORD, "John", "Doe");
        User savedUser = repo.save(testUser);
        Integer userId = savedUser.getId();

        User userUpdateUserRoles = repo.findById(userId).get();
        Role roleEditor = new Role(3);
        Role roleSalesperson = new Role(2);

        userUpdateUserRoles.getRoles().remove(roleEditor);
        userUpdateUserRoles.addRole(roleSalesperson);

        repo.save(userUpdateUserRoles);

        User updatedUser = repo.findById(userId).get();
        assertThat(updatedUser.getRoles()).contains(roleSalesperson);

        repo.deleteById(userId); // Cleanup
    }

    @Test
    public void testDeleteUser() {
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";
        User testUser = new User(uniqueEmail, BASE_PASSWORD, "John", "Doe");
        User savedUser = repo.save(testUser);
        Integer userIdToDelete = savedUser.getId();

        // Delete the test user
        repo.deleteById(userIdToDelete);

        // Assert that the user is deleted
        Optional<User> deletedUser = repo.findById(userIdToDelete);
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    public void testGetUserByEmail() {
        String email = "ya@a.com";
        User userByEmail = repo.getUserByEmail(email);

        assertThat(userByEmail).isNotNull();
    }

    @Test
    public void testCountById() {
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";
        User testUser = new User(uniqueEmail, BASE_PASSWORD, "John", "Doe");
        User savedUser = repo.save(testUser);
        Integer userId = savedUser.getId();

        Long countById = repo.countById(userId);
        assertThat(countById).isNotNull().isGreaterThan(0);

        repo.deleteById(userId); // Cleanup
    }

    @Test
    public void testEnableUser() {
        Integer id = 3;
        repo.updateEnabledStatus(id, true);
    }

    @Test
    public void testDisableUser() {
        String uniqueEmail = UUID.randomUUID().toString() + "@test.com";
        User testUser = new User(uniqueEmail, BASE_PASSWORD, "John", "Doe");
        User savedUser = repo.save(testUser);
        Integer userId = savedUser.getId();

        repo.updateEnabledStatus(userId, false);

        User updatedUser = repo.findById(userId).get();
        assertThat(updatedUser.isEnabled()).isFalse();

        repo.deleteById(userId); // Cleanup

    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);

        List<User> listUsers = page.getContent();
        assertThat(listUsers.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsers() {
        String keyword = "Remzi";

        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(keyword, pageable);

        List<User> listUsers = page.getContent();

        listUsers.forEach(user -> System.out.println(user));

        assertThat(listUsers.size()).isGreaterThan(0);
    }
}