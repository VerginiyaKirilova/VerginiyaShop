package com.shopme.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.shopme.common.entity.*;
import com.shopme.common.entity.order.*;
import com.shopme.common.entity.product.Product;
import com.shopme.repository.CustomerRepository;
import com.shopme.repository.OrderRepository;
import com.shopme.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.repository.OrderDetailRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderDetailRepositoryTests {

    @Autowired
    private OrderDetailRepository repo;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    private Customer customer;
    Integer savedCustomerId;
    Integer savedProductId;
    Integer savedOrderId;
    Integer savedOrderDetailId;
    Integer savedOrderTrackId;

    @BeforeEach
    @Transactional
    public void setUp() {
        Integer countryId = 234;
        Country country = entityManager.find(Country.class, countryId);
        Brand brand = entityManager.find(Brand.class, 37);
        Category category = entityManager.find(Category.class, 5);

        customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Daniela");
        customer.setLastName("Davide");
        customer.setPassword("password123");
        customer.setEmail("Daniela.s.fountaine@gmail.com");
        customer.setPhoneNumber("312-462-7518");
        customer.setAddressLine1("1927 West Drive");
        customer.setCity("Sacramento");
        customer.setState("California");
        customer.setPostalCode("95867");
        customer.setVerificationCode("code_456");
        customer.setAuthenticationType(AuthenticationType.DATABASE);
        customer.setCreatedTime(new Date());
        customer.setEnabled(true);
        entityManager.persist(customer);


        Product product = new Product();
        product.setAlias("alias-new");
        product.setName("Product NEW");
        product.setCost(120);
        product.setPrice(150);
        product.setFullDescription("Detailed description of the product 744.");
        product.setShortDescription("Short description for Acer Aspire 744");
        product.setEnabled(true);
        product.setCreatedTime(new Date());
        product.setDiscountPercent(0);
        product.setHeight(15.0f);
        product.setInStock(true);
        product.setLength(20.0f);
        product.setMainImage("default.png");
        product.setReviewCount(50);
        product.setUpdatedTime(new Date());
        product.setWeight(1.5f);
        product.setWidth(10.0f);
        product.setBrand(brand);
        product.setCategory(category);
        entityManager.persist(product);


        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderTime(new Date());
        order.setStatus(OrderStatus.DELIVERED);
        order.copyAddressFromCustomer();
        order.setShippingCost(10);
        order.setProductCost(product.getCost());
        order.setTax(0);
        order.setSubtotal(product.getPrice());
        order.setTotal(product.getPrice() + 10);
        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        order.setDeliverDate(new Date());
        order.setDeliverDays(1);
        entityManager.persist(order);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());
        entityManager.persist(orderDetail);

        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setOrder(order);
        orderTrack.setStatus(OrderStatus.DELIVERED);
        orderTrack.setUpdatedTime(new Date());
        entityManager.persist(orderTrack);


        savedCustomerId = customer.getId();
        savedProductId = product.getId();
        savedOrderId = order.getId();
        savedOrderDetailId = orderDetail.getId();
        savedOrderTrackId = orderTrack.getId();
    }

    @AfterEach
    public void tearDown() {
        if (savedOrderDetailId != null) {
            repo.deleteById(savedOrderDetailId);
        }
        if (savedOrderId != null) {
            orderRepository.deleteById(savedOrderId);
        }
        if (savedProductId != null) {
            productRepository.deleteById(savedProductId);
        }
        if (savedCustomerId != null) {
            customerRepository.deleteById(savedCustomerId);
        }
    }

    @Test
    public void testCountByProductAndCustomerAndOrderStatus() {
        Integer productId = savedProductId;
        Integer customerId = savedCustomerId;

        Long count = repo.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.DELIVERED);
        assertThat(count).isGreaterThan(0);
    }

}
