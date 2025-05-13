package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.dto.UserVoucherDTO;
import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.entites.UserVoucher;
import fpt.aptech.projectbe.repository.UserRepository;
import fpt.aptech.projectbe.repository.UserVouchersRepository;
import fpt.aptech.projectbe.repository.UserVouchersRepository;
import fpt.aptech.projectbe.service.UserVouchersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserVouchersServiceImpl implements UserVouchersService {

    @Autowired
    private UserVouchersRepository userVoucherRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserVoucher> findAll() {
        return userVoucherRepository.findAll();
    }

    @Override
    public UserVoucher findById(Integer id) {
        return userVoucherRepository.findById(id).orElse(null);
    }

    @Override
    public UserVoucher save(UserVoucherDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Tìm xem đã có voucher với code và user này chưa
        Optional<UserVoucher> existingVoucherOpt = userVoucherRepository.findByCodeAndUser(dto.getCode(), user);

        if (existingVoucherOpt.isPresent()) {
            UserVoucher existingVoucher = existingVoucherOpt.get();
            existingVoucher.setCountCode(existingVoucher.getCountCode() + 1); // tăng count
            return userVoucherRepository.save(existingVoucher);
        }

        // Nếu chưa có, tạo mới
        UserVoucher voucher = new UserVoucher();
        voucher.setCode(dto.getCode());
        voucher.setDiscountType(dto.getDiscountType());
        voucher.setDiscountValue(dto.getDiscountValue());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());
        voucher.setUsed(dto.getUsed());
        voucher.setUser(user);
        voucher.setCountCode(1); // khởi tạo count = 1

        return userVoucherRepository.save(voucher);
    }



    @Override
    public void deleteById(Integer id) {
        userVoucherRepository.deleteById(id);
    }

    @Override
    public UserVoucher update(UserVoucher userVoucher) {
        if (userVoucher.getId() == null || !userVoucherRepository.existsById(userVoucher.getId())) {
            return null;
        }
        return userVoucherRepository.save(userVoucher);
    }

    @Override
    public UserVoucher useVoucherByUserId(Integer userId, String code) {
        Optional<UserVoucher> optionalVoucher = userVoucherRepository
                .findByUserIdAndCodeAndUsedFalse(userId, code);

        if (optionalVoucher.isPresent()) {
            UserVoucher voucher = optionalVoucher.get();

            if (voucher.getUsed()) {
                System.out.println("Voucher đã được sử dụng.");
                return null;
            }

            LocalDateTime now = LocalDateTime.now();
            if ((voucher.getStartDate() == null || !now.isBefore(voucher.getStartDate())) &&
                    (voucher.getEndDate() == null || !now.isAfter(voucher.getEndDate()))) {
                return voucher; // Còn hạn, chưa dùng
            } else {
                return null;
            }
        }

        return null;
    }

    @Override
    public List<UserVoucher> findAllByUserId(Integer userId) {
        return userVoucherRepository.findByUserId(userId);
    }

    @Override
    public UserVoucher setCountVoucherByUserId(Integer userId, String code) {
        UserVoucher voucher = userVoucherRepository.findByUserIdAndCodeAndUsedFalse(userId, code)
                .orElseThrow(() -> new RuntimeException("Voucher not found or already used"));
        voucher.setCountCode(voucher.getCountCode() - 1);

        // Nếu sau khi trừ count = 0 thì set used = true (tùy logic của bạn)
        if (voucher.getCountCode() == 0) {
            userVoucherRepository.deleteById(voucher.getId());
        }

        return userVoucherRepository.save(voucher);

    }


}
