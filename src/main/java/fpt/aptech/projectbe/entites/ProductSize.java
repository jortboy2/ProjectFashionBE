package fpt.aptech.projectbe.entites;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "product_sizes")
public class ProductSize {
    @EmbeddedId
    private ProductSizeId id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sizeId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "size_id")
    private Size size;

    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    public ProductSizeId getId() {
        return id;
    }

    public void setId(ProductSizeId id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
} 