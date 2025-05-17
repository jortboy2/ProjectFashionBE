package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Cart;

public interface CartService {
    Cart getCartByUserId(int userId);
    Cart addToCart(int userId, int productId, int sizeId, int quantity);
    Cart updateItem(int cartItemId, int quantity);
    void removeItem(int cartItemId);
    void clearCart(int userId);
}
