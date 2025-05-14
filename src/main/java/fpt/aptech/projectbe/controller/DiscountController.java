package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.Discount;
import fpt.aptech.projectbe.service.DiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "*")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // Lấy tất cả mã giảm giá
    @GetMapping
    public List<Discount>  getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    // Lấy mã giảm giá theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Integer id) {
        Discount discount = discountService.getDiscountById(id);
        return ResponseEntity.ok(discount);
    }

    // Lấy mã giảm giá theo mã code
    @GetMapping("/code/{code}")
    public ResponseEntity<Discount> getDiscountByCode(@PathVariable String code) {
        Discount discount = discountService.getDiscountByCode(code);
        if (discount != null) {
            return ResponseEntity.ok(discount);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Tạo mã giảm giá mới
    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        Discount created = discountService.createDiscount(discount);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Cập nhật mã giảm giá
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable Integer id, @RequestBody Discount discount) {
        try {
            // Debug log to see what we received
            System.out.println("Received update request for ID: " + id);
            System.out.println("Discount code: " + discount.getCode());
            System.out.println("Discount type: " + discount.getDiscountType());
            System.out.println("Discount value: " + discount.getDiscountValue());
            System.out.println("Start date: " + discount.getStartDate());
            System.out.println("End date: " + discount.getEndDate());
            
            Discount updated = discountService.updateDiscount(id, discount);
            
            // Debug log after update
            System.out.println("Updated discount: " + updated.getCode());
            System.out.println("Updated type: " + updated.getDiscountType());
            System.out.println("Updated value: " + updated.getDiscountValue());
            
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Xoá mã giảm giá
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Integer id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<?> validateDiscountCode(@PathVariable String code) {
        try {
            Discount discount = discountService.validateDiscountCode(code);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Mã giảm giá hợp lệ");
            response.put("discount_type", discount.getDiscountType());
            response.put("discount_value", discount.getDiscountValue());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }



}
