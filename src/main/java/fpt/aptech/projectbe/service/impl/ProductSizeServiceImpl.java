package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.repository.ProductSizeRepository;
import fpt.aptech.projectbe.service.ProductSizeService;
import fpt.aptech.projectbe.service.ProductService;
import fpt.aptech.projectbe.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductSizeServiceImpl implements ProductSizeService {

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private SizeService sizeService;

    @Override
    public List<ProductSize> findAll() {
        return productSizeRepository.findAll();
    }

    @Override
    public Optional<ProductSize> findById(Integer id) {
        return productSizeRepository.findById(id);
    }

    @Override
    public ProductSize save(ProductSize productSize) {
        return productSizeRepository.save(productSize);
    }

    @Override
    public ProductSize update(ProductSize productSize) {
        return productSizeRepository.save(productSize);
    }

    @Override
    public void deleteById(Integer id) {
        productSizeRepository.deleteById(id);
    }

    @Override
    public void delete(ProductSize productSize) {
        productSizeRepository.delete(productSize);
    }

    @Override
    public ProductSize findByProductAndSize(Product product, Size size) {
        return productSizeRepository.findByProductAndSize(product, size);
    }

    @Override
    public List<ProductSize> findByProduct(Product product) {
        return productSizeRepository.findByProduct(product);
    }

    @Override
    public List<ProductSize> findBySize(Size size) {
        return productSizeRepository.findBySize(size);
    }

    @Override
    public List<ProductSize> findByProductId(Integer productId) {
        Product product = productService.findById(productId);
        if (product == null) {
            return List.of();
        }
        return findByProduct(product);
    }

    @Override
    public ProductSize findByProductIdAndSizeId(Integer productId, Integer sizeId) {
        Product product = productService.findById(productId);
        Size size = sizeService.findById(sizeId);
        if (product == null || size == null) {
            return null;
        }
        return findByProductAndSize(product, size);
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