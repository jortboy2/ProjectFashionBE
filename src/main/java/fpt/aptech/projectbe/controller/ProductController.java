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
        // Lấy hình ảnh và category cho từng sản phẩm
        for (Product product : products) {
            // Lấy hình ảnh
            List<ProductImage> images = productImageService.findByProduct(product);
            product.setProductImages(images);
            
            // Lấy category
            if (product.getCategory() != null) {
                Category category = categoryService.findById(product.getCategory().getId());
                product.setCategory(category);
            }
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
            List<ProductImage> images = productImageService.findByProduct(product);
            product.setProductImages(images);
            product.setCategory(category);
        }
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm");
        }
        // Lấy hình ảnh và category của sản phẩm
        List<ProductImage> images = productImageService.findByProduct(product);
        product.setProductImages(images);
        
        // Lấy thông tin category
        if (product.getCategory() != null) {
            Category category = categoryService.findById(product.getCategory().getId());
            product.setCategory(category);
        }
        
        return ResponseEntity.ok(product);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("stock") String stock,
            @RequestParam("categoryId") Integer categoryId, // <-- Thêm dòng này
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            // Tạo sản phẩm mới
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(new java.math.BigDecimal(price));

            // Gán Category cho Product
            Category category = categoryService.findById(categoryId);
            if (category == null) {
                return ResponseEntity.badRequest().body("Invalid categoryId: " + categoryId);
            }
            product.setCategory(category); // <-- Gán category cho product

            // Lưu sản phẩm trước
            Product savedProduct = productService.save(product);

            // Upload ảnh
            if (images != null && !images.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                List<ProductImage> productImages = new ArrayList<>();
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);

                        String imageUrl = "/images/products/" + fileName;

                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(savedProduct);
                        productImage.setImageUrl(imageUrl);
                        productImages.add(productImage);
                    }
                }

                if (!productImages.isEmpty()) {
                    productImageService.saveAll(productImages);
                }
            }

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
