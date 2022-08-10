package org.example.productservice.repositories;

import org.example.productservice.model.EntityStatus;
import org.example.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndStatus(Long id, EntityStatus status);

    boolean existsByNameAndStatus(String name, EntityStatus status);

    Long countProductsByStatus(EntityStatus status);

    Page<Product> findAllByStatus(EntityStatus status, Pageable pageable);

}
