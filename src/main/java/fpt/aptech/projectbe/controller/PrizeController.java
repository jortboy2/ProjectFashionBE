package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.WheelPrize;
import fpt.aptech.projectbe.service.PrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prizes")
@CrossOrigin(origins = "*")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    @GetMapping
    public ResponseEntity<List<WheelPrize>> getAllPrizes() {
        return ResponseEntity.ok(prizeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPrizeById(@PathVariable Integer id) {
        WheelPrize prize = prizeService.findById(id);
        if (prize == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy phần thưởng");
        }
        return ResponseEntity.ok(prize);
    }

    @PostMapping
    public ResponseEntity<WheelPrize> createPrize(@RequestBody WheelPrize prize) {
        return ResponseEntity.ok(prizeService.save(prize));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrize(@PathVariable Integer id, @RequestBody WheelPrize prize) {
        WheelPrize existing = prizeService.findById(id);
        if (existing == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy phần thưởng để cập nhật");
        }

        existing.setLabel(prize.getLabel());
        existing.setColor(prize.getColor());
        existing.setCode(prize.getCode());
        existing.setValue(prize.getValue());
        existing.setDiscountType(prize.getDiscountType());

        return ResponseEntity.ok(prizeService.update(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrize(@PathVariable Integer id) {
        if (prizeService.findById(id) == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy phần thưởng để xoá");
        }
        prizeService.deleteById(id);
        return ResponseEntity.ok("Xoá thành công");
    }
}
