package com.spring.productmanagement.controller;

import com.spring.productmanagement.dto.ProductDTO;
import com.spring.productmanagement.errorhandling.ResourceNotFoundException;
import com.spring.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<String> createProductWithSubProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult bindingResult) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            // If there are validation errors, return a response with error details
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder errorMessage = new StringBuilder("Validation error(s): ");
            for (FieldError error : errors) {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        // If no validation errors, proceed with creating the product
        productService.createProductWithSubProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product and SubProduct created successfully");
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        try {
            ProductDTO productDTO = productService.getProductById(productId);
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Product not found with id " + productId, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateProductWithSubProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO
    ) {
        try {
            productService.updateProductAndSubProduct(productId, productDTO);
            return new ResponseEntity<>("Product and SubProduct updated successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProductWithSubProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductAndSubProduct(productId);
            return new ResponseEntity<>("Product and SubProduct deleted successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productsPage = productService.getAllProductsWithPagination(pageable);

        if (productsPage.isEmpty()) {
            return new ResponseEntity<>("No products available", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(productsPage, HttpStatus.OK);
    }

}
