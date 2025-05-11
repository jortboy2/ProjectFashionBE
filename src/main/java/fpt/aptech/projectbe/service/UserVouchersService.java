package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.entites.UserVoucher;

import java.util.List;

public interface UserVouchersService {
    List<UserVoucher> findAll();
    UserVoucher findById(Integer id);
    UserVoucher save(UserVoucher user);
    void deleteById(Integer id);
    UserVoucher update(UserVoucher user);
    UserVoucher useVoucherByUserId(Integer userId, String code);

} 