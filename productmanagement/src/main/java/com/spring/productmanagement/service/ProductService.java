package com.spring.productmanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.productmanagement.dto.ProductDTO;
import com.spring.productmanagement.dto.SubProductsDTO;
import com.spring.productmanagement.errorhandling.ResourceNotFoundException;
import com.spring.productmanagement.model.Product;
import com.spring.productmanagement.model.SubProduct;
import com.spring.productmanagement.repo.ProductRepo;
import com.spring.productmanagement.repo.SubProductRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private SubProductRepo subProductRepo;

    @Autowired
    private ObjectMapper objectMapper;

    /*@PostConstruct
    public void initializeData() {

        try {
            ClassPathResource resource = new ClassPathResource("initialData.json");
            List<ProductDTO> products = Arrays.asList(objectMapper.readValue(resource.getInputStream(), ProductDTO[].class));

            for (ProductDTO productDTO : products) {
                createProductWithSubProduct(productDTO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Transactional
    public void createProductWithSubProduct(ProductDTO productDTO){
        Product product = new Product();
        product.setProduct_name(productDTO.getName());
        product.setProduct_desc(productDTO.getDesc());

        productRepo.save(product);

        productDTO.getSubProductsDTOList().stream()
                .map(subProductsDTO -> {
                    SubProduct subProduct = new SubProduct();
                    subProduct.setSub_product_name(subProductsDTO.getName());
                    subProduct.setSub_product_desc(subProductsDTO.getDesc());
                    subProduct.setProduct(product);
                    return subProduct;
                }).forEach(subProductRepo::save);
    }

    public ProductDTO getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return mapToProductDTO(product);
        } else {
            throw new ResourceNotFoundException("Product and SubProducts are not found with id" + productId);
        }
    }

    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getProduct_id());
        productDTO.setName(product.getProduct_name());
        productDTO.setDesc(product.getProduct_desc());

        List<SubProductsDTO> subProductsDTOList = product.getSubProducts()
                .stream()
                .filter(Objects::nonNull)  // Ensure subProduct is not null
                .map(subProduct -> {
                    SubProductsDTO subProductDTO = new SubProductsDTO();
                    subProductDTO.setId(subProduct.getSub_product_id());
                    subProductDTO.setName(subProduct.getSub_product_name());
                    subProductDTO.setDesc(subProduct.getSub_product_desc());
                    return subProductDTO;
                })
                .collect(Collectors.toList());

        productDTO.setSubProductsDTOList(subProductsDTOList);
        return productDTO;
    }

    @Transactional
    public void updateProductAndSubProduct(Long productId, ProductDTO productDTO) {
        Optional<Product> optionalProduct = productRepo.findById(productId);

        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            existingProduct.setProduct_name(productDTO.getName());
            existingProduct.setProduct_desc(productDTO.getDesc());

            updateSubProducts(existingProduct, productDTO.getSubProductsDTOList());

            productRepo.save(existingProduct);
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
    }

    private void updateSubProducts(Product product, List<SubProductsDTO> subProductsDTOList) {
        List<SubProduct> existingSubProducts = product.getSubProducts();
        List<SubProduct> updatedSubProducts = new ArrayList<>();

        if (subProductsDTOList != null) {
            for (SubProductsDTO subProductDTO : subProductsDTOList) {
                SubProduct existingSubProduct = findExistingSubProduct(existingSubProducts, subProductDTO);

                if (existingSubProduct != null) {
                    // Update existing SubProduct
                    existingSubProduct.setSub_product_name(subProductDTO.getName());
                    existingSubProduct.setSub_product_desc(subProductDTO.getDesc());
                    existingSubProducts.add(existingSubProduct);
                } else {
                    // Create a new SubProduct
                    SubProduct newSubProduct = new SubProduct();
                    newSubProduct.setSub_product_name(subProductDTO.getName());
                    newSubProduct.setSub_product_desc(subProductDTO.getDesc());
                    newSubProduct.setProduct(product);
                    updatedSubProducts.add(newSubProduct);
                }
            }
        }

        // Remove any existing SubProducts not present in the updated list
        existingSubProducts.removeAll(updatedSubProducts);

        // Save or update the updated SubProducts
        subProductRepo.saveAll(updatedSubProducts);
    }

    private SubProduct findExistingSubProduct(List<SubProduct> existingSubProducts, SubProductsDTO subProductDTO) {
        return existingSubProducts.stream()
                .filter(subProduct -> Objects.equals(subProduct.getSub_product_name(), subProductDTO.getName()))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void deleteProductAndSubProduct(Long productId) {
        Optional<Product> optionalProduct = productRepo.findById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            deleteSubProducts(product);
            productRepo.delete(product);
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
    }

    private void deleteSubProducts(Product product) {
        List<SubProduct> subProducts = product.getSubProducts();
        if (subProducts != null && !subProducts.isEmpty()) {
            subProductRepo.deleteAll(subProducts);
        }
    }
    public Page<ProductDTO> getAllProductsWithPagination(Pageable pageable) {
        Page<Product> productsPage = (Page<Product>) productRepo.findAll(pageable);

        return productsPage.map(this::mapToProductDTO);
    }
}
