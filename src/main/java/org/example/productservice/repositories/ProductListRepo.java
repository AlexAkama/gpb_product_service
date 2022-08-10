package org.example.productservice.repositories;

import org.example.productservice.model.EntityStatus;
import org.example.productservice.model.ProductList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductListRepo extends JpaRepository<ProductList, Long> {

    Optional<ProductList> findByIdAndStatus(Long id, EntityStatus status);

    boolean existsByNameAndStatus(String name, EntityStatus status);

    Long countProductListByStatus(EntityStatus status);

    Page<ProductList> findAllByStatus(EntityStatus status, Pageable pageable);

}
