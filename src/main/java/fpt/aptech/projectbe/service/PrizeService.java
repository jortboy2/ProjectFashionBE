package fpt.aptech.projectbe.service;


import fpt.aptech.projectbe.entites.WheelPrize;

import java.util.List;

public interface PrizeService {
    List<WheelPrize> findAll();
    WheelPrize findById(Integer id);
    WheelPrize save(WheelPrize wheelPrize);
    WheelPrize update(WheelPrize wheelPrize);
    void deleteById(Integer id);
}
