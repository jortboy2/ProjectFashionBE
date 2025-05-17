package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.dto.CartDTO;
import fpt.aptech.projectbe.entites.Cart;
import fpt.aptech.projectbe.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable int userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(new CartDTO(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @RequestParam int userId,
            @RequestParam int productId,
            @RequestParam int sizeId,
            @RequestParam int quantity) {
        Cart cart = cartService.addToCart(userId, productId, sizeId, quantity);
        return ResponseEntity.ok(new CartDTO(cart));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartDTO> updateItem(@PathVariable int cartItemId, @RequestParam int quantity) {
        Cart cart = cartService.updateItem(cartItemId, quantity);
        return ResponseEntity.ok(new CartDTO(cart));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable int cartItemId) {
        cartService.removeItem(cartItemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable int userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
