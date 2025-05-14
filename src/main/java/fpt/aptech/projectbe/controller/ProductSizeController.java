package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.dto.CreateProductSizeDTO;
import fpt.aptech.projectbe.dto.ProductSizeDTO;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.entites.ProductSizeId;
import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.service.ProductSizeService;
import fpt.aptech.projectbe.service.ProductService;
import fpt.aptech.projectbe.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product-sizes")
@CrossOrigin(origins = "*")
public class ProductSizeController {
    @Autowired
    private ProductSizeService productSizeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SizeService sizeService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductSizeDTO>> getProductSizes(@PathVariable Integer productId) {
        List<ProductSize> productSizes = productSizeService.findByProductId(productId);
        List<ProductSizeDTO> dtos = productSizes.stream()
                .map(ps -> new ProductSizeDTO(
                        ps.getProduct().getId(),
                        ps.getSize().getId(),
                        ps.getSize().getName(),
                        ps.getStock()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/size/{sizeId}")
    public ResponseEntity<List<ProductSizeDTO>> getProductsBySize(@PathVariable Integer sizeId) {
        Size size = sizeService.findById(sizeId);
        if (size == null) {
            return ResponseEntity.notFound().build();
        }

        List<ProductSize> productSizes = productSizeService.findBySize(size);
        List<ProductSizeDTO> dtos = productSizes.stream()
                .map(ps -> new ProductSizeDTO(
                        ps.getProduct().getId(),
                        ps.getSize().getId(),
                        ps.getSize().getName(),
                        ps.getStock()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/product/{productId}/size/{sizeId}")
    public ResponseEntity<ProductSizeDTO> getProductSize(@PathVariable Integer productId, @PathVariable Integer sizeId) {
        ProductSize productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
        if (productSize != null) {
            ProductSizeDTO dto = new ProductSizeDTO(
                    productSize.getProduct().getId(),
                    productSize.getSize().getId(),
                    productSize.getSize().getName(),
                    productSize.getStock());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ProductSizeDTO> createProductSize(@RequestBody CreateProductSizeDTO dto) {
        Product product = productService.findById(dto.getProductId());
        Size size = sizeService.findById(dto.getSizeId());
        
        if (product == null || size == null) {
            return ResponseEntity.badRequest().build();
        }

        ProductSize productSize = new ProductSize();
        ProductSizeId id = new ProductSizeId(dto.getProductId(), dto.getSizeId());
        productSize.setId(id);
        productSize.setProduct(product);
        productSize.setSize(size);
        productSize.setStock(dto.getStock());

        ProductSize saved = productSizeService.save(productSize);
        ProductSizeDTO responseDto = new ProductSizeDTO(
                saved.getProduct().getId(),
                saved.getSize().getId(),
                saved.getSize().getName(),
                saved.getStock());
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/product/{productId}/size/{sizeId}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Integer productId,
            @PathVariable Integer sizeId,
            @RequestParam Integer quantity) {
        productSizeService.updateStock(productId, sizeId, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/product/{productId}/size/{sizeId}/add-stock")
    public ResponseEntity<?> addStock(
            @PathVariable Integer productId,
            @PathVariable Integer sizeId,
            @RequestParam Integer quantity) {
        try {
            ProductSize productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
            if (productSize == null) {
                return ResponseEntity.notFound().build();
            }

            if (quantity <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Số lượng thêm vào phải lớn hơn 0"));
            }

            // Cập nhật số lượng mới = số lượng hiện tại + số lượng thêm vào
            int newStock = productSize.getStock() + quantity;
            productSizeService.updateStock(productId, sizeId, newStock);

            return ResponseEntity.ok(Map.of(
                "message", "Cập nhật số lượng thành công",
                "oldStock", productSize.getStock(),
                "addedQuantity", quantity,
                "newStock", newStock
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Lỗi khi cập nhật số lượng: " + e.getMessage()));
        }
    }

    @PutMapping("/product/{productId}/size/{sizeId}/adjust-stock")
    public ResponseEntity<?> adjustStock(
            @PathVariable Integer productId,
            @PathVariable Integer sizeId,
            @RequestParam Integer adjustment) {
        try {
            ProductSize productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
            if (productSize == null) {
                return ResponseEntity.notFound().build();
            }

            int currentStock = productSize.getStock();
            int newStock = currentStock + adjustment;

            // Validate if the new stock would be negative
            if (newStock < 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "message", "Không thể giảm số lượng xuống dưới 0",
                        "currentStock", currentStock,
                        "attemptedAdjustment", adjustment
                    ));
            }

            // Update the stock
            productSizeService.updateStock(productId, sizeId, newStock);

            return ResponseEntity.ok(Map.of(
                "message", "Điều chỉnh số lượng thành công",
                "productId", productId,
                "sizeId", sizeId,
                "oldStock", currentStock,
                "adjustment", adjustment,
                "newStock", newStock
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Lỗi khi điều chỉnh số lượng: " + e.getMessage()));
        }
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<List<ProductSizeDTO>> getOutOfStockProducts() {
        List<ProductSize> allProductSizes = productSizeService.findAll();
        List<ProductSizeDTO> outOfStockProducts = allProductSizes.stream()
            .filter(ps -> ps.getStock() <= 0)
            .map(ps -> new ProductSizeDTO(
                ps.getProduct().getId(),
                ps.getSize().getId(),
                ps.getSize().getName(),
                ps.getStock()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(outOfStockProducts);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductSizeDTO>> getLowStockProducts(@RequestParam(defaultValue = "5") Integer threshold) {
        List<ProductSize> allProductSizes = productSizeService.findAll();
        List<ProductSizeDTO> lowStockProducts = allProductSizes.stream()
            .filter(ps -> ps.getStock() > 0 && ps.getStock() <= threshold)
            .map(ps -> new ProductSizeDTO(
                ps.getProduct().getId(),
                ps.getSize().getId(),
                ps.getSize().getName(),
                ps.getStock()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(lowStockProducts);
    }

    @DeleteMapping("/product/{productId}/size/{sizeId}")
    public ResponseEntity<Void> deleteProductSize(@PathVariable Integer productId, @PathVariable Integer sizeId) {
        ProductSize productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
        if (productSize != null) {
            productSizeService.delete(productSize);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 