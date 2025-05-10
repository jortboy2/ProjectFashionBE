package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.Category;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.ProductImage;
import fpt.aptech.projectbe.service.CategoryService;
import fpt.aptech.projectbe.service.ProductService;
import fpt.aptech.projectbe.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImageService productImageService;

    @Value("${server.port:8080}")
    private String serverPort;

    private final String UPLOAD_DIR = "src/main/resources/static/images/products/";
    private final String BASE_URL = "http://localhost:";

    private void loadProductDetails(Product product) {
        // Load images
        List<ProductImage> images = productImageService.findByProduct(product);
        product.setProductImages(images);
        
        // Load category
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryService.findById(product.getCategory().getId());
            if (category != null) {
                product.setCategory(category);
            }
        }
    }

    @GetMapping("/related/{productId}")
    public ResponseEntity<?> getRelatedProducts(@PathVariable Integer productId) {
        // Lấy sản phẩm hiện tại
        Product currentProduct = productService.findById(productId);
        if (currentProduct == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm");
        }

        // Kiểm tra category
        if (currentProduct.getCategory() == null) {
            return ResponseEntity.ok(new ArrayList<>()); // Trả về danh sách rỗng nếu không có category
        }

        // Lấy các sản phẩm liên quan
        List<Product> relatedProducts = productService.findRelatedProducts(
            currentProduct.getCategory().getId(), 
            productId
        );

        // Load thông tin chi tiết cho từng sản phẩm
        for (Product product : relatedProducts) {
            loadProductDetails(product);
        }

        return ResponseEntity.ok(relatedProducts);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        for (Product product : products) {
            loadProductDetails(product);
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable Integer categoryId) {
        // Kiểm tra category có tồn tại không
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục");
        }

        // Lấy danh sách sản phẩm theo category
        List<Product> products = productService.findByCategoryId(categoryId);
        
        // Lấy hình ảnh và category cho từng sản phẩm
        for (Product product : products) {
            loadProductDetails(product);
        }
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm");
        }
        loadProductDetails(product);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.save(product);
            loadProductDetails(savedProduct);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo sản phẩm: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        try {
            Product existingProduct = productService.findById(id);
            if (existingProduct == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm để cập nhật");
            }
            product.setId(id);
            Product updatedProduct = productService.update(product);
            loadProductDetails(updatedProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            Product product = productService.findById(id);
            if (product == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm để xoá");
            }
            productService.deleteById(id);
            return ResponseEntity.ok("Xoá sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xoá sản phẩm: " + e.getMessage());
        }
    }
}
