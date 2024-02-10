package com.spring.productmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Table(name="Product")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long product_id;

    private String product_name;
    private String product_desc;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<SubProduct> subProducts;
}
