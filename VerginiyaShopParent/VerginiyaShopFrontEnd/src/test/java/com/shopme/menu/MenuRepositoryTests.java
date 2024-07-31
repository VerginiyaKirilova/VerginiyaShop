package com.shopme.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;
import com.shopme.repository.MenuRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MenuRepositoryTests {

    @Autowired
    private MenuRepository repo;
    private Menu menu;
    private Menu footerMenu;
    private Menu menuAlias;

    @BeforeEach
    public void setUp() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setEnabled(true);
        menu.setPosition(1);
        menu.setTitle("Unique Header Menu Item");
        menu.setAlias("regarding");
        repo.save(menu);

        Menu footerMenu = new Menu();
        footerMenu.setType(MenuType.FOOTER);
        footerMenu.setEnabled(true);
        footerMenu.setPosition(1);
        footerMenu.setTitle("Footer Menu Item");
        footerMenu.setAlias("footer");
        repo.save(footerMenu);

        Menu menuAlias = new Menu();
        menuAlias.setType(MenuType.FOOTER);
        menuAlias.setEnabled(true);
        menuAlias.setPosition(1);
        menuAlias.setTitle("Privacy Policy");
        menuAlias.setAlias("privacy-policy");
        repo.save(menuAlias);
    }

    @AfterEach
    public void tearDown() {
        if (menu != null && menu.getId() != null) {
            repo.deleteById(menu.getId());
        } if (footerMenu != null && footerMenu.getId() != null) {
            repo.deleteById(footerMenu.getId());
        } if (menuAlias != null && menuAlias.getId() != null) {
            repo.deleteById(menuAlias.getId());
        }
    }

    @Test
    public void testListHeaderMenuItems() {
        List<Menu> listHeaderMenuItems = repo.findByTypeAndEnabledOrderByPositionAsc(MenuType.HEADER, true);
        assertThat(listHeaderMenuItems).isNotEmpty();

        listHeaderMenuItems.forEach(System.out::println);
    }

    @Test
    public void testListFooterMenuItems() {
        List<Menu> listFooterMenuItems = repo.findByTypeAndEnabledOrderByPositionAsc(MenuType.FOOTER, true);
        assertThat(listFooterMenuItems).isNotEmpty();

        listFooterMenuItems.forEach(System.out::println);
    }

    @Test
    public void testFindMenuByAliasNotFound() {
        String alias = "test-alias";
        Menu menu = repo.findByAlias(alias);
        assertThat(menu).isNull();
    }

    @Test
    public void testFindMenuByAliasFound() {
        String alias = "privacy-policy";
        Menu menu = repo.findByAlias(alias);
        assertThat(menu).isNotNull();
    }
}