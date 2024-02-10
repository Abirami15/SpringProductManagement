package com.spring.productmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"Product Id", "Name", "Description","SubProducts"})
public class ProductDTO {
    @JsonProperty("Product Id")
    private Long id;

    @NotBlank(message = "Product name should not be blank")
    @JsonProperty("Name")
    private String name;

    @NotBlank(message = "Product name should not be blank")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "SubProduct description should contain only alphabets and numbers")
    @JsonProperty("Description")
    private String desc;

    @JsonProperty("SubProducts")
    private List<SubProductsDTO> subProductsDTOList;
}
