package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.ProductImage;
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
    private ProductImageService productImageService;

    @Value("${server.port:8080}")
    private String serverPort;

    private final String UPLOAD_DIR = "src/main/resources/static/images/products/";
    private final String BASE_URL = "http://localhost:";

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        // Lấy hình ảnh cho từng sản phẩm
        for (Product product : products) {
            List<ProductImage> images = productImageService.findByProduct(product);
            product.setProductImages(images);
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm");
        }
        // Lấy hình ảnh của sản phẩm
        List<ProductImage> images = productImageService.findByProduct(product);
        product.setProductImages(images);
        return ResponseEntity.ok(product);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("stock") String stock,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            // Tạo sản phẩm mới
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(new java.math.BigDecimal(price));
            product.setStock(Integer.parseInt(stock));

            // Lưu sản phẩm trước
            Product savedProduct = productService.save(product);

            // Xử lý upload hình ảnh nếu có
            if (images != null && !images.isEmpty()) {
                // Tạo thư mục nếu chưa tồn tại
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                List<ProductImage> productImages = new ArrayList<>();
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        // Tạo tên file duy nhất
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        
                        // Lưu file
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);

                        // Tạo URL để truy cập hình ảnh
                        String imageUrl = "/images/products/" + fileName;

                        // Tạo ProductImage với Product đã được lưu
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(savedProduct);
                        productImage.setImageUrl(imageUrl);
                        productImages.add(productImage);
                    }
                }

                // Lưu tất cả hình ảnh
                if (!productImages.isEmpty()) {
                    productImageService.saveAll(productImages);
                }
            }

            // Lấy lại sản phẩm với hình ảnh
            return ResponseEntity.ok(productService.findById(savedProduct.getId()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to create product: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "stock", required = false) String stock,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        
        Product existingProduct = productService.findById(id);
        if (existingProduct == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm để cập nhật");
        }

        try {
            // Cập nhật thông tin sản phẩm
            if (name != null) existingProduct.setName(name);
            if (description != null) existingProduct.setDescription(description);
            if (price != null) existingProduct.setPrice(new java.math.BigDecimal(price));
            if (stock != null) existingProduct.setStock(Integer.parseInt(stock));

            // Lưu sản phẩm đã cập nhật
            Product updatedProduct = productService.update(existingProduct);

            // Xử lý upload hình ảnh mới nếu có
            if (images != null && !images.isEmpty()) {
                // Tạo thư mục nếu chưa tồn tại
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                List<ProductImage> productImages = new ArrayList<>();
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        // Tạo tên file duy nhất
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        
                        // Lưu file
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);

                        // Tạo URL để truy cập hình ảnh
                        String imageUrl = "/images/products/" + fileName;

                        // Tạo ProductImage với Product đã được cập nhật
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(updatedProduct);
                        productImage.setImageUrl(imageUrl);
                        productImages.add(productImage);
                    }
                }

                // Lưu tất cả hình ảnh mới
                if (!productImages.isEmpty()) {
                    productImageService.saveAll(productImages);
                }
            }

            // Lấy lại sản phẩm với hình ảnh mới
            return ResponseEntity.ok(productService.findById(updatedProduct.getId()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to update product: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm để xoá");
        }

        try {
            // Xóa tất cả hình ảnh của sản phẩm
            List<ProductImage> productImages = productImageService.findByProduct(product);
            for (ProductImage image : productImages) {
                // Xóa file
                String fileName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.deleteIfExists(filePath);
            }

            // Xóa sản phẩm (cascade sẽ tự động xóa các ProductImage trong database)
            productService.deleteById(id);
            return ResponseEntity.ok("Xoá sản phẩm thành công");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to delete product: " + e.getMessage());
        }
    }
}
