package com.spring.productmanagement.repo;

import com.spring.productmanagement.model.SubProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubProductRepo extends JpaRepository<SubProduct,Long> {
    @Query(value = "SELECT * FROM sub_product WHERE product_id = :productId", nativeQuery = true)
    List<SubProduct> findSubProductsByProductIdNative(Long productId);
}
