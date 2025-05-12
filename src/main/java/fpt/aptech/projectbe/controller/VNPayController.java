package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestParam Long orderId, @RequestParam long amount) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(orderId, amount);
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating payment: " + e.getMessage());
        }
    }

    @GetMapping("/payment-callback")
    public ResponseEntity<?> paymentCallback(
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TxnRef") String orderId,
            @RequestParam("vnp_Amount") String amount,
            @RequestParam("vnp_TransactionStatus") String transactionStatus) {
        
        Map<String, String> response = new HashMap<>();
        
        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            response.put("status", "success");
            response.put("message", "Payment successful");
            response.put("orderId", orderId);
            response.put("amount", amount);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failed");
            response.put("message", "Payment failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 