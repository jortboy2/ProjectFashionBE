package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.repository.ProductRepository;
import fpt.aptech.projectbe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 1. Lấy tất cả sản phẩm
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // 2. Tìm sản phẩm theo ID
    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    // 3. Lưu sản phẩm mới
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    // 4. Xoá sản phẩm theo ID
    @Override
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    // 5. Cập nhật sản phẩm
    @Override
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> findRelatedProducts(Integer categoryId, Integer productId) {
        return productRepository.findRelatedProducts(categoryId, productId);
    }
}
