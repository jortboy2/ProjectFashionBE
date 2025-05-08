package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.ProductImage;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.service.ProductImageService;
import fpt.aptech.projectbe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@CrossOrigin(origins = "*")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductImage>> getAllProductImages() {
        return ResponseEntity.ok(productImageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductImage> getProductImageById(@PathVariable Integer id) {
        return productImageService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImage>> getProductImagesByProduct(@PathVariable Integer productId) {
        return productService.findById(productId)
                .map(product -> ResponseEntity.ok(productImageService.findByProduct(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductImage> createProductImage(@RequestBody ProductImage productImage) {
        return ResponseEntity.ok(productImageService.save(productImage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductImage> updateProductImage(@PathVariable Integer id, @RequestBody ProductImage productImage) {
        return productImageService.findById(id)
                .map(existingProductImage -> {
                    productImage.setId(id);
                    return ResponseEntity.ok(productImageService.update(productImage));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Integer id) {
        return productImageService.findById(id)
                .map(productImage -> {
                    productImageService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 