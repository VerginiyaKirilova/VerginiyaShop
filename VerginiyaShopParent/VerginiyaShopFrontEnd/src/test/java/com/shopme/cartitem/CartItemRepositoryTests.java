package com.shopme.cartitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.List;

import com.shopme.common.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.product.Product;
import com.shopme.repository.CartItemRepository;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Transactional
public class CartItemRepositoryTests {
    @Autowired
    private CartItemRepository repo;
    @Autowired
    private TestEntityManager entityManager;
    private Integer customerId;
    private Integer productId1;
    private Integer productId2;
    private Integer cartItemId1;
    private Integer cartItemId2;
    private Integer countryId;

    @Autowired
    public CartItemRepositoryTests(CartItemRepository repo, TestEntityManager entityManager) {
        super();
        this.repo = repo;
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void setUp() {
        Country country = new Country();
        country.setName("CountryNameNew");
        country.setCode("CN");
        entityManager.persist(country);
        countryId = country.getId();

        Customer customer = new Customer();
        customer.setEmail("test3@example.com");
        customer.setPassword("password");
        customer.setFirstName("Johne");
        customer.setLastName("Dole");
        customer.setCountry(country);
        customer.setAddressLine1("123 Main St");
        customer.setAddressLine2("130 Main St");
        customer.setPhoneNumber("1234567890");
        customer.setCity("New York");
        customer.setState("New York");
        customer.setPostalCode("10001");
        customer.setEnabled(true);
        customer.setAuthenticationType(AuthenticationType.DATABASE);
        customer.setCreatedTime(new Date());
        entityManager.persist(customer);
        customerId = customer.getId();

        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setAlias("product1Alias");
        product1.setCreatedTime(new Date());
        product1.setFullDescription("Full description for product 1");
        product1.setMainImage("default1.png");
        product1.setShortDescription("Short description for product 1");
        product1.setAverageRating(4.5f);
        product1.setCost(19.99f);
        product1.setDiscountPercent(10);
        product1.setEnabled(true);
        product1.setHeight(10.0f);
        product1.setInStock(true);
        product1.setLength(15.0f);
        product1.setPrice(29.99f);
        product1.setReviewCount(5);
        product1.setUpdatedTime(new Date());
        product1.setWeight(0.5f);
        product1.setWidth(20.0f);

        entityManager.persist(product1);
        productId1 = product1.getId();


        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setAlias("product2Alias");
        product2.setCreatedTime(new Date());
        product2.setFullDescription("Full description for product 2");
        product2.setMainImage("default2.png");
        product2.setShortDescription("Short description for product 2");
        product2.setAverageRating(4.0f);
        product2.setCost(25.99f);
        product2.setDiscountPercent(5);
        product2.setEnabled(true);
        product2.setHeight(12.0f);
        product2.setInStock(true);
        product2.setLength(20.0f);
        product2.setPrice(35.99f);
        product2.setReviewCount(10);
        product2.setUpdatedTime(new Date());
        product2.setWeight(0.8f);
        product2.setWidth(25.0f);

        entityManager.persist(product2);
        productId2 = product2.getId();

        CartItem item1 = new CartItem();
        item1.setCustomer(customer);
        item1.setProduct(product1);
        item1.setQuantity(1);
        CartItem savedItem1 = entityManager.persist(item1);
        cartItemId1 = savedItem1.getId();

        CartItem item2 = new CartItem();
        item2.setCustomer(customer);
        item2.setProduct(product2);
        item2.setQuantity(2);
        CartItem savedItem2 = entityManager.persist(item2);
        cartItemId2 = savedItem2.getId();
    }

    @AfterEach
    public void tearDown() {
        if (cartItemId1 != null) repo.deleteById(cartItemId1);
        if (cartItemId2 != null) repo.deleteById(cartItemId2);

        if (productId1 != null) {
            Product product1 = entityManager.find(Product.class, productId1);
            if (product1 != null) entityManager.remove(product1);
        }

        if (productId2 != null) {
            Product product2 = entityManager.find(Product.class, productId2);
            if (product2 != null) entityManager.remove(product2);
        }

        if (customerId != null) {
            Customer customer = entityManager.find(Customer.class, customerId);
            if (customer != null) entityManager.remove(customer);
        }

        if (countryId != null) {
            Country country = entityManager.find(Country.class, countryId);
            if (country != null) entityManager.remove(country);
        }
    }

    @Test
    public void testSaveItem() {
        Integer customerId = 1;
        Integer productId = 1;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(1);

        CartItem savedItem = repo.save(newItem);

        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testSave2Items() {
        Integer customerId = 19;
        Integer productId = 7;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem item1 = new CartItem();
        item1.setCustomer(customer);
        item1.setProduct(product);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setCustomer(new Customer(customerId));
        item2.setProduct(new Product(6));
        item2.setQuantity(3);

        Iterable<CartItem> iterable = repo.saveAll(List.of(item1, item2));

        assertThat(iterable).size().isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        List<CartItem> listItems = repo.findByCustomer(new Customer(customerId));

        listItems.forEach(System.out::println);

        assertThat(listItems.size()).isEqualTo(2);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Customer testCustomer = new Customer();
        testCustomer.setId(customerId);

        Product testProduct = new Product();
        testProduct.setId(productId1);

        CartItem item = repo.findByCustomerAndProduct(testCustomer, testProduct);


        assertNotNull(item, "The CartItem should not be null");
    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = this.customerId;
        Integer productId = this.productId1;
        Integer quantity = 1;

        repo.updateQuantity(quantity, customerId, productId);


        CartItem item = repo.findByCustomerAndProduct(new Customer(customerId), new Product(productId));


        assertNotNull(item, "CartItem should not be null");


        assertThat(item.getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Integer customerId = 10;
        Integer productId = 10;

        repo.deleteByCustomerAndProduct(customerId, productId);

        CartItem item = repo.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNull();
    }
}