package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.ProductImage;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.repository.ProductImageRepository;
import fpt.aptech.projectbe.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    @Override
    public Optional<ProductImage> findById(Integer id) {
        return productImageRepository.findById(id);
    }

    @Override
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public void deleteById(Integer id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public ProductImage update(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public List<ProductImage> findByProduct(Product product) {
        return productImageRepository.findByProduct(product);
    }
} 