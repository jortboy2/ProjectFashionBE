package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.repository.SizeRepository;
import fpt.aptech.projectbe.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public List<Size> findAll() {
        return sizeRepository.findAll();
    }

    @Override
    public Size findById(Integer id) {
        return sizeRepository.findById(id).orElse(null);
    }

    @Override
    public Size findByName(String name) {
        return sizeRepository.findByName(name);
    }

    @Override
    public Size save(Size size) {
        return sizeRepository.save(size);
    }

    @Override
    public void deleteById(Integer id) {
        sizeRepository.deleteById(id);
    }
} 