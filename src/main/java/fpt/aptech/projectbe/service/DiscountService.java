package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Discount;

import java.util.List;

public interface DiscountService {
    List<Discount> getAllDiscounts();

    Discount createDiscount(Discount discount);

    Discount getDiscountById(Integer id);

    Discount getDiscountByCode(String code);

    Discount updateDiscount(Integer id, Discount discount);

    void deleteDiscount(Integer id);

    Discount validateDiscountCode(String code);
}
