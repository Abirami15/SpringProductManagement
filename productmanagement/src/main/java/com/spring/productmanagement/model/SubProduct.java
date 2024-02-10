package com.spring.productmanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="SubProduct")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SubProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sub_product_id;

    private String sub_product_name;
    private String sub_product_desc;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
