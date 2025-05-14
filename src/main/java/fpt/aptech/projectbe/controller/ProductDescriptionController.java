package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.service.impl.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductDescriptionController {

    private final GeminiService geminiService;

    public ProductDescriptionController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

  
    
    @PostMapping("/generate-detailed-description")
    public ResponseEntity<?> generateDetailedDescription(
        @RequestParam("image") MultipartFile image,
        @RequestParam("name") String name
    ) {
        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng chọn hình ảnh");
            }
            
            // Kiểm tra định dạng file
            String contentType = image.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg"))) {
                return ResponseEntity.badRequest().body("Chỉ chấp nhận file JPG, JPEG hoặc PNG");
            }
            
            StringBuilder prompt = new StringBuilder("Viết mô tả hấp dẫn chi tiết cho sản phẩm thời trang có tên: " + name);

            prompt.append(". Mô tả nên bao gồm chất liệu, kiểu dáng, màu sắc, phong cách và các đặc điểm nổi bật.");

            
            String description = geminiService.generateCustomDescriptionFromImage(image, prompt.toString());
            return ResponseEntity.ok(description);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi tạo mô tả: " + e.getMessage());
        }
    }
}
