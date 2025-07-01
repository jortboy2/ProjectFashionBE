package fpt.aptech.projectbe.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.HashMap;
import java.util.Base64;

@Service
public class PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createPaymentUrl(Long orderId, Long amount, String returnUrl, String cancelUrl) {
        try {
            // Get access token
            String accessToken = getAccessToken();
            
            // Create payment
            String paymentId = createPayment(orderId, amount, returnUrl, cancelUrl, accessToken);
            
            // Return PayPal approval URL (redirect user to www.sandbox.paypal.com)
            return "https://www.sandbox.paypal.com/checkoutnow?token=" + paymentId;
        } catch (Exception e) {
            throw new RuntimeException("Error creating PayPal payment URL: " + e.getMessage());
        }
    }

    public String createPaymentUrl(Long orderId, Long amount) {
        return createPaymentUrl(orderId, amount, null, null);
    }

    public boolean executePayment(String paymentId, String payerId) {
        try {
            String accessToken = getAccessToken();
            
            // Execute payment
            String url = baseUrl + "/v1/payments/payment/" + paymentId + "/execute";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("payer_id", payerId);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Error executing PayPal payment: " + e.getMessage());
        }
    }

    public Integer getOrderIdFromToken(String token) {
        try {
            // In a real implementation, you would store the mapping between token and orderId
            // For now, we'll extract orderId from the token if it contains it
            if (token.contains("order_")) {
                String orderIdStr = token.substring(token.indexOf("order_") + 6);
                return Integer.parseInt(orderIdStr);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error extracting order ID from token: " + e.getMessage());
        }
    }

    private String getAccessToken() {
        try {
            String url = baseUrl + "/v1/oauth2/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, clientSecret);
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
            
            throw new RuntimeException("Failed to get PayPal access token");
        } catch (Exception e) {
            throw new RuntimeException("Error getting PayPal access token: " + e.getMessage());
        }
    }

    private String createPayment(Long orderId, Long amount, String returnUrl, String cancelUrl, String accessToken) {
        try {
            String url = baseUrl + "/v1/payments/payment";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            
            Map<String, Object> payment = new HashMap<>();
            payment.put("intent", "sale");
            
            // Payer
            Map<String, Object> payer = new HashMap<>();
            payer.put("payment_method", "paypal");
            payment.put("payer", payer);
            
            // Transactions
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("description", "Order #" + orderId);
            
            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("currency", "USD");
            amountMap.put("total", amount.toString());
            transaction.put("amount", amountMap);
            
            payment.put("transactions", new Object[]{transaction});
            
            // Redirect URLs
            Map<String, Object> redirectUrls = new HashMap<>();
            String orderIdParam = "orderId=" + orderId;
            if (returnUrl != null) {
                if (returnUrl.contains("?")) {
                    redirectUrls.put("return_url", returnUrl + "&" + orderIdParam);
                } else {
                    redirectUrls.put("return_url", returnUrl + "?" + orderIdParam);
                }
            } else {
                redirectUrls.put("return_url", "http://localhost:5173/order-success?" + orderIdParam);
            }
            if (cancelUrl != null) {
                redirectUrls.put("cancel_url", cancelUrl);
            } else {
                redirectUrls.put("cancel_url", "http://localhost:5173/order-failed");
            }
            payment.put("redirect_urls", redirectUrls);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payment, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("id");
            }
            
            throw new RuntimeException("Failed to create PayPal payment");
        } catch (Exception e) {
            throw new RuntimeException("Error creating PayPal payment: " + e.getMessage());
        }
    }
} 