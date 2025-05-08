//package fpt.aptech.projectbe.controller;
//
//import fpt.aptech.projectbe.entites.Payment;
//import fpt.aptech.projectbe.entites.Product;
//import fpt.aptech.projectbe.service.PaymentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//public class PaymentController {
//
//    @Autowired
//    private PaymentService paymentService;
//    @GetMapping
//    public ResponseEntity<List<Payment>> getAllPayment() {
//        return ResponseEntity.ok(paymentService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getPaymentById(@PathVariable Integer id) {
//        Payment payment = paymentService.findById(id);
//        if (payment == null) {
//            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm");
//        }
//        return ResponseEntity.ok(payment);
//    }
//
//    @PostMapping
//    public ResponseEntity<Payment> createPayment(@RequestBody Payment product) {
//        return ResponseEntity.ok(paymentService.save(product));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updatePayment(@PathVariable Integer id, @RequestBody Payment product) {
//        if (paymentService.findById(id) == null) {
//            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm để cập nhật");
//        }
//        product.setId(id);
//        return ResponseEntity.ok(paymentService.update(product));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deletePayment(@PathVariable Integer id) {
//        if (paymentService.findById(id) == null) {
//            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm để xoá");
//        }
//        paymentService.deleteById(id);
//        return ResponseEntity.ok("Xoá sản phẩm thành công");
//    }
//}
