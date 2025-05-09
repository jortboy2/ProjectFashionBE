package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@CrossOrigin(origins = "*")
public class SizeController {
    @Autowired
    private SizeService sizeService;

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
    public ResponseEntity<Size> updateSize(@PathVariable Integer id, @RequestBody Size size) {
        Size existingSize = sizeService.findById(id);
        if (existingSize != null) {
            size.setId(id);
            return ResponseEntity.ok(sizeService.save(size));
        }
        return ResponseEntity.notFound().build();
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
} 