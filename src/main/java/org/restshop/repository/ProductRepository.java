package org.restshop.repository;

import org.restshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by name
     */
    Optional<Product> findByName(String name);
    /**
     * Find products by price range
     */
    List<Product> findByPriceBetween(BigDecimal priceMin, BigDecimal priceMax);

    /**
     * Find all active products
     */
    List<Product> findByActiveTrue();

    /**
     * Custom query to find products by name and active status
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.active = true")
    List<Product> findActiveProductsByName(@Param("name") String name);

}
