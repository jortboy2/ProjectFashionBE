package fpt.aptech.projectbe.dto;

public class CreateProductSizeDTO {
    private Integer productId;
    private Integer sizeId;
    private Integer stock;

    public CreateProductSizeDTO() {
    }

    public CreateProductSizeDTO(Integer productId, Integer sizeId, Integer stock) {
        this.productId = productId;
        this.sizeId = sizeId;
        this.stock = stock;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getSizeId() {
        return sizeId;
    }

    public void setSizeId(Integer sizeId) {
        this.sizeId = sizeId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
} 