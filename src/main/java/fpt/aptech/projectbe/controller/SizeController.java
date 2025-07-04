package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.dto.ProductSizeSimpleDTO;
import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.service.SizeService;
import fpt.aptech.projectbe.service.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sizes")
@CrossOrigin(origins = "*")
public class SizeController {
    @Autowired
    private SizeService sizeService;
    @Autowired
    private ProductSizeService productSizeService;

    @GetMapping
    public ResponseEntity<List<Size>> getAllSizes() {
        return ResponseEntity.ok(sizeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Size> getSizeById(@PathVariable Integer id) {
        Size size = sizeService.findById(id);
        if (size != null) {
            return ResponseEntity.ok(size);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Size> createSize(@RequestBody Size size) {
        return ResponseEntity.ok(sizeService.save(size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSize(@PathVariable Integer id, @RequestBody Size size) {
        Map<String, Object> response = new HashMap<>();
        
        // Kiểm tra size có tồn tại không
        Size existingSize = sizeService.findById(id);
        if (existingSize == null) {
            response.put("success", false);
            response.put("message", "Size không tồn tại với ID: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate dữ liệu đầu vào
        if (size.getName() == null || size.getName().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tên size không được để trống");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (size.getCatesize() == null || size.getCatesize().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Danh mục size không được để trống");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Kiểm tra tên size có bị trùng với size khác không
        Size sizeWithSameName = sizeService.findByName(size.getName().trim());
        if (sizeWithSameName != null && !sizeWithSameName.getId().equals(id)) {
            response.put("success", false);
            response.put("message", "Tên size '" + size.getName() + "' đã tồn tại");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Cập nhật thông tin size
            existingSize.setName(size.getName().trim());
            existingSize.setCatesize(size.getCatesize().trim());
            
            Size updatedSize = sizeService.save(existingSize);
            
            response.put("success", true);
            response.put("message", "Cập nhật size thành công");
            response.put("data", updatedSize);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật size: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable Integer id) {
        Size size = sizeService.findById(id);
        if (size != null) {
            sizeService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductSizeSimpleDTO>> getSizesByProductId(@PathVariable Integer productId) {
        List<ProductSize> productSizes = productSizeService.findByProductId(productId);
        if (productSizes == null || productSizes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ProductSizeSimpleDTO> dtos = productSizes.stream()
                .map(ps -> new ProductSizeSimpleDTO(
                        ps.getProduct().getId(),
                        ps.getSize().getId(),
                        ps.getSize().getName(),
                        ps.getSize().getCatesize()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
} 