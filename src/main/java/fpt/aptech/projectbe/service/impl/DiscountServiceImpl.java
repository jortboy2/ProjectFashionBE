package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Discount;
import fpt.aptech.projectbe.repository.DiscountRepository;
import fpt.aptech.projectbe.service.DiscountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public Discount createDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    @Override
    public Discount getDiscountById(Integer id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
    }

    @Override
    public Discount getDiscountByCode(String code) {
        return discountRepository.findByCode(code);
    }

    @Override
    public Discount updateDiscount(Integer id, Discount newDiscount) {
        Discount discount = getDiscountById(id);
        discount.setCode(newDiscount.getCode());
        discount.setDiscountType(newDiscount.getDiscountType());
        discount.setDiscountValue(newDiscount.getDiscountValue());
        discount.setStartDate(newDiscount.getStartDate());
        discount.setEndDate(newDiscount.getEndDate());
        return discountRepository.save(discount);
    }

    @Override
    public void deleteDiscount(Integer id) {
        discountRepository.deleteById(id);
    }

    @Override
    public Discount validateDiscountCode(String code) throws IllegalArgumentException {
        Discount discount = discountRepository.findByCode(code);

        if (discount == null) {
            throw new IllegalArgumentException("Mã giảm giá không tồn tại");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(discount.getStartDate())) {
            throw new IllegalArgumentException("Mã giảm giá chưa bắt đầu");
        }

        if (now.isAfter(discount.getEndDate())) {
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn");
        }

        return discount; // hợp lệ
    }


}
