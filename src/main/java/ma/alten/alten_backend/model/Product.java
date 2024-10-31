package ma.alten.alten_backend.model;

import jakarta.persistence.*;
import lombok.*;
import ma.alten.alten_backend.enumeration.InventoryStatus;
import ma.alten.alten_backend.util.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
    private String description;
    @Column(nullable = true, length = 64)
    private String image;
    private String category;
    private Double price;
    private Integer quantity;
    private String internalReference;
    private Long shellId;
    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus;
    private Double rating;
    private Boolean deleted;

}
