package org.restshop.repository;

import org.restshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Find product by name (case-insensitive)
     * Used for duplicate validation during product creation
     *
     * @param name the product name to search for
     * @return Optional containing the product if found, empty otherwise
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Find products by price range
     *
     * @param priceMin minimum price (inclusive)
     * @param priceMax maximum price (inclusive)
     * @return list of products within the specified price range
     */
    List<Product> findByPriceBetween(BigDecimal priceMin, BigDecimal priceMax);

    /**
     * Find all active products
     *
     * @return list of all products where active = true
     */
    List<Product> findByActiveTrue();

    /**
     * Find all active products with pagination support
     *
     * @param pageable pagination information (page number, size, sort)
     * @return page of active products
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Custom query to find active products by name (partial match, case-insensitive)
     * Use this method by passing the search term with wildcards from the service layer
     * Example: findActiveProductsByName("%laptop%")
     *
     * @param name the search pattern with wildcards (e.g., "%searchTerm%")
     * @return list of active products matching the name pattern
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name) AND p.active = true")
    List<Product> findActiveProductsByName(@Param("name") String name);

}
