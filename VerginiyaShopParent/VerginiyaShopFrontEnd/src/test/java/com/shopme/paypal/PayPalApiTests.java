package com.shopme.paypal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.shopme.PayPalCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.shopme.checkout.PayPalOrderResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    @Test
    public void testGetOrderDetails() {
        // Извличане на креденшали от AWS Secrets Manager
        Map<String, String> credentials = PayPalCredentials.getPayPalCredentials();
        String clientId = credentials.get("AWS_ACCESS_KEY_ID");
        String clientSecret = credentials.get("AWS_SECRET_ACCESS_KEY");

        String orderId = "4A027975W0474063L";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
                requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

        assertNotNull(orderResponse);
        System.out.println("Order ID: " + orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));
    }
//    private static final String BASE_URL = "https://api.sandbox.paypal.com";
//    private static final String GET_ORDER_API = "/v2/checkout/orders/";
//    private static final String CLIENT_ID = "PAYPAL_CLIENT_ID";
//    private static final String CLIENT_SECRET = "PAYPAL_CLIENT_SECRET";
//
//    @Test
//    public void testGetOrderDetails() {
//        String orderId = "4A027975W0474063L";
//        String requestURL = BASE_URL + GET_ORDER_API + orderId;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.add("Accept-Language", "en_US");
//        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
//        RestTemplate restTemplate = new RestTemplate();
//
//        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
//                requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
//        PayPalOrderResponse orderResponse = response.getBody();
//
//        System.out.println("Order ID: " + orderResponse.getId());
//        System.out.println("Validated: " + orderResponse.validate(orderId));
//
//    }
}