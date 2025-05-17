package fpt.aptech.projectbe.dto;

import fpt.aptech.projectbe.entites.Cart;
import fpt.aptech.projectbe.entites.CartItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private Integer id;
    private Instant createdAt;
    private List<CartItemDTO> items = new ArrayList<>();

    public CartDTO() {
    }

    public CartDTO(Cart cart) {
        this.id = cart.getId();
        this.createdAt = cart.getCreatedAt();
        this.items = cart.getItems().stream()
                .map(CartItemDTO::new)
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public static class CartItemDTO {
        private Integer id;
        private Integer productId;
        private String productName;
        private Integer sizeId;
        private String sizeName;
        private Integer quantity;
        private Double price;

        public CartItemDTO() {
        }

        public CartItemDTO(CartItem item) {
            this.id = item.getId();
            this.productId = item.getProduct().getId();
            this.productName = item.getProduct().getName();
            this.sizeId = item.getSize().getId();
            this.sizeName = item.getSize().getName();
            this.quantity = item.getQuantity();
            this.price = item.getPrice().doubleValue();
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getSizeId() {
            return sizeId;
        }

        public void setSizeId(Integer sizeId) {
            this.sizeId = sizeId;
        }

        public String getSizeName() {
            return sizeName;
        }

        public void setSizeName(String sizeName) {
            this.sizeName = sizeName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }
} 