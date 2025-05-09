package fpt.aptech.projectbe.dto;

public class ProductSizeDTO {
    private Integer productId;
    private Integer sizeId;
    private String sizeName;
    private Integer stock;

    public ProductSizeDTO() {
    }

    public ProductSizeDTO(Integer productId, Integer sizeId, String sizeName, Integer stock) {
        this.productId = productId;
        this.sizeId = sizeId;
        this.sizeName = sizeName;
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

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
} 