package fpt.aptech.projectbe.dto;

public class ProductSizeSimpleDTO {
    private Integer productId;
    private Integer sizeId;
    private String sizeName;
    private String catesize;

    public ProductSizeSimpleDTO(Integer productId, Integer sizeId, String sizeName, String catesize) {
        this.productId = productId;
        this.sizeId = sizeId;
        this.sizeName = sizeName;
        this.catesize = catesize;
    }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getSizeId() { return sizeId; }
    public void setSizeId(Integer sizeId) { this.sizeId = sizeId; }

    public String getSizeName() { return sizeName; }
    public void setSizeName(String sizeName) { this.sizeName = sizeName; }

    public String getCatesize() { return catesize; }
    public void setCatesize(String catesize) { this.catesize = catesize; }
} 