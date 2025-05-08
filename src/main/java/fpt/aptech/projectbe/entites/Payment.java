package fpt.aptech.projectbe.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @ColumnDefault("'completed'")
    @Column(name = "status", length = 50)
    private String status;

/*
 TODO [Reverse Engineering] create field to map the 'payment_date' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "payment_date", columnDefinition = "timestamp not null")
    private Object paymentDate;
*/
}