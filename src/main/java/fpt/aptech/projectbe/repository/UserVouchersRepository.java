package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.entites.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVouchersRepository extends JpaRepository<UserVoucher, Integer> {
    Optional<UserVoucher> findByUserIdAndCodeAndUsedFalse(Integer userId, String code);
    List<UserVoucher> findByUserId(Integer userId);
    Optional<UserVoucher> findByUserIdAndCode(Integer userId, String code);
    Optional<UserVoucher> findByCodeAndUser(String code, User user);

} 