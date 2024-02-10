package com.spring.productmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
@Getter
@Setter
@JsonPropertyOrder({"SubProduct Id","Name","Description"})
public class SubProductsDTO {
    @JsonProperty("SubProduct Id")
    private Long id;

    @JsonProperty("Name")
    @NotBlank(message = "SubProduct name should not be null")
    private String name;

    @JsonProperty("Description")
    @NotBlank(message = "SubProduct description should not be null")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "SubProduct description should contain only alphabets and numbers")
    private String desc;

    public SubProductsDTO(){
    }

    public SubProductsDTO(String name, String desc){
        this.name=name;
        this.desc=desc;
    }

}
