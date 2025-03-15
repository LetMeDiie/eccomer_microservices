package kz.amihady.eccomerce.image.entity;

import jakarta.persistence.*;
import kz.amihady.eccomerce.product.entity.Product;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
