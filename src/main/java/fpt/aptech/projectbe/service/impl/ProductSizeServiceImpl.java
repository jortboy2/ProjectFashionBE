package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.repository.ProductSizeRepository;
import fpt.aptech.projectbe.service.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductSizeServiceImpl implements ProductSizeService {
    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Override
    public List<ProductSize> findByProductId(Integer productId) {
        return productSizeRepository.findByProductId(productId);
    }

    @Override
    public ProductSize findByProductIdAndSizeId(Integer productId, Integer sizeId) {
        return productSizeRepository.findByProductIdAndSizeId(productId, sizeId);
    }

    @Override
    public ProductSize save(ProductSize productSize) {
        return productSizeRepository.save(productSize);
    }

    @Override
    public void delete(ProductSize productSize) {
        productSizeRepository.delete(productSize);
    }

    @Override
    @Transactional
    public void updateStock(Integer productId, Integer sizeId, Integer quantity) {
        ProductSize productSize = findByProductIdAndSizeId(productId, sizeId);
        if (productSize != null) {
            productSize.setStock(quantity);
            save(productSize);
        }
    }
} 