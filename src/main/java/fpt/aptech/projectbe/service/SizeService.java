package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Size;
import java.util.List;

public interface SizeService {
    List<Size> findAll();
    Size findById(Integer id);
    Size findByName(String name);
    Size save(Size size);
    void deleteById(Integer id);
} 