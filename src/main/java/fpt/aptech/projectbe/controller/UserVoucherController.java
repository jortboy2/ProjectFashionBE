package fpt.aptech.projectbe.controller;


import fpt.aptech.projectbe.entites.UserVoucher;
import fpt.aptech.projectbe.service.UserVouchersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin("*")
public class UserVoucherController {

    @Autowired
    private UserVouchersService userVouchersService;

    @GetMapping
    public List<UserVoucher> getAll() {
        return userVouchersService.findAll();
    }

    @GetMapping("/{id}")
    public UserVoucher getById(@PathVariable Integer id) {
        return userVouchersService.findById(id);
    }

    @PostMapping
    public UserVoucher create(@RequestBody UserVoucher userVoucher) {
        return userVouchersService.save(userVoucher);
    }

    @PutMapping("/{id}")
    public UserVoucher update(@PathVariable Integer id, @RequestBody UserVoucher userVoucher) {
        userVoucher.setId(id);
        return userVouchersService.update(userVoucher);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userVouchersService.deleteById(id);
    }
    @GetMapping("/use")
    public ResponseEntity<?> useVoucher(@RequestParam Integer userId, @RequestParam String code) {
        UserVoucher usedVoucher = userVouchersService.useVoucherByUserId(userId, code);
        if (usedVoucher != null) {
            return ResponseEntity.ok(usedVoucher);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Voucher không hợp lệ hoặc đã hết hạn.");
    }

}
