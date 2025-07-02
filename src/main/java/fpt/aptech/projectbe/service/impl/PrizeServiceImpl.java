package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.WheelPrize;
import fpt.aptech.projectbe.repository.PrizeRepository;
import fpt.aptech.projectbe.service.PrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrizeServiceImpl implements PrizeService {

    @Autowired
    private PrizeRepository prizeRepository;

    @Override
    public List<WheelPrize> findAll() {
        return prizeRepository.findAll();
    }

    @Override
    public WheelPrize findById(Integer id) {
        return prizeRepository.findById(id).orElse(null);
    }

    @Override
    public WheelPrize save(WheelPrize wheelPrize) {
        return prizeRepository.save(wheelPrize);
    }

    @Override
    public WheelPrize update(WheelPrize wheelPrize) {
        return prizeRepository.save(wheelPrize);
    }

    @Override
    public void deleteById(Integer id) {
        prizeRepository.deleteById(id);
    }
}
