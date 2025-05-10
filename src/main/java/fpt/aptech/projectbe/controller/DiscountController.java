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
    public ResponseEntity<Discount> updateDiscount(@PathVariable Integer id, @RequestBody Discount discount) {
        Discount updated = discountService.updateDiscount(id, discount);
        return ResponseEntity.ok(updated);
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
