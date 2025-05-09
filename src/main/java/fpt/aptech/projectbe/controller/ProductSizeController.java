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