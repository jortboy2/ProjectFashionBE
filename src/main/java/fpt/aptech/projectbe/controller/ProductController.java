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
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "existingImageIds", required = false) String existingImageIdsJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        Product existingProduct = productService.findById(id);
        if (existingProduct == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm để cập nhật");
        }

        try {
            System.out.println("Updating product ID: " + id);
            System.out.println("Received existingImageIds: " + existingImageIdsJson);
            
            // Cập nhật thông tin sản phẩm
            if (name != null) existingProduct.setName(name);
            if (description != null) existingProduct.setDescription(description);
            if (price != null) existingProduct.setPrice(new java.math.BigDecimal(price));

            // Cập nhật category nếu có
            if (categoryId != null) {
                Category category = categoryService.findById(categoryId);
                if (category == null) {
                    return ResponseEntity.badRequest().body("Không tìm thấy danh mục với ID: " + categoryId);
                }
                existingProduct.setCategory(category);
            }

            // Lưu sản phẩm đã cập nhật
            Product updatedProduct = productService.update(existingProduct);
            
            // Lấy tất cả ảnh hiện tại
            List<ProductImage> currentImages = productImageService.findByProduct(updatedProduct);
            System.out.println("Current images count: " + currentImages.size());
            
            // Nếu không có existingImageIds và không có ảnh mới, giữ nguyên ảnh cũ
            if ((existingImageIdsJson == null || existingImageIdsJson.isEmpty()) 
                && (images == null || images.isEmpty())) {
                System.out.println("No image changes requested - keeping all existing images");
                return ResponseEntity.ok(productService.findById(updatedProduct.getId()));
            }
            
            // Parse danh sách ID ảnh cần giữ lại
            List<Integer> existingImageIds = new ArrayList<>();
            if (existingImageIdsJson != null && !existingImageIdsJson.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    existingImageIds = java.util.Arrays.asList(mapper.readValue(existingImageIdsJson, Integer[].class));
                    System.out.println("Parsed existingImageIds: " + existingImageIds);
                } catch (Exception e) {
                    System.err.println("Error parsing existingImageIds: " + e.getMessage());
                    e.printStackTrace();
                    // Không return lỗi, tiếp tục xử lý các ảnh mới nếu có
                }
            }
            
            // Xóa các ảnh không còn trong danh sách giữ lại
            for (ProductImage image : currentImages) {
                if (!existingImageIds.contains(image.getId())) {
                    System.out.println("Deleting image ID: " + image.getId());
                    // Xóa file physical
                    try {
                        String fileName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
                        Path filePath = Paths.get(UPLOAD_DIR, fileName);
                        Files.deleteIfExists(filePath);
                        System.out.println("Deleted file: " + fileName);
                    } catch (Exception e) {
                        System.err.println("Error deleting image file: " + e.getMessage());
                    }
                    
                    // Xóa record trong DB
                    try {
                        productImageService.deleteById(image.getId());
                        System.out.println("Deleted DB record for image ID: " + image.getId());
                    } catch (Exception e) {
                        System.err.println("Error deleting image record: " + e.getMessage());
                    }
                } else {
                    System.out.println("Keeping image ID: " + image.getId());
                }
            }
            
            // Upload ảnh mới nếu có
            if (images != null && !images.isEmpty()) {
                System.out.println("Processing " + images.size() + " new images");
                
                // Tạo thư mục upload nếu chưa có
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    System.out.println("Created upload directory: " + uploadPath);
                }
                
                List<ProductImage> newImages = new ArrayList<>();
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        try {
                            String originalFilename = file.getOriginalFilename();
                            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
                            Path filePath = uploadPath.resolve(fileName);
                            Files.copy(file.getInputStream(), filePath);
                            
                            String imageUrl = "/images/products/" + fileName;
                            System.out.println("Uploaded new image: " + imageUrl);
                            
                            ProductImage productImage = new ProductImage();
                            productImage.setProduct(updatedProduct);
                            productImage.setImageUrl(imageUrl);
                            newImages.add(productImage);
                        } catch (Exception e) {
                            System.err.println("Error uploading image: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                
                if (!newImages.isEmpty()) {
                    try {
                        productImageService.saveAll(newImages);
                        System.out.println("Saved " + newImages.size() + " new images to database");
                    } catch (Exception e) {
                        System.err.println("Error saving new images to database: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            // Reload product với images mới
            Product finalProduct = productService.findById(updatedProduct.getId());
            List<ProductImage> finalImages = productImageService.findByProduct(finalProduct);
            System.out.println("Final image count: " + finalImages.size());
            
            return ResponseEntity.ok(finalProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi cập nhật sản phẩm: " + e.getMessage());
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
