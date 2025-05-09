package fpt.aptech.projectbe.entites;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductSizeId implements Serializable {
    private Integer productId;
    private Integer sizeId;

    public ProductSizeId() {
    }

    public ProductSizeId(Integer productId, Integer sizeId) {
        this.productId = productId;
        this.sizeId = sizeId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSizeId that = (ProductSizeId) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(sizeId, that.sizeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, sizeId);
    }
} 