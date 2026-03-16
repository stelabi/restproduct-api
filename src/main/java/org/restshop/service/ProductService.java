package org.restshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restshop.dto.ProductDTO;
import org.restshop.dto.ProductResponseDTO;
import org.restshop.entity.Product;
import org.restshop.exception.ProductNotFoundException;
import org.restshop.mapper.ProductMapper;
import org.restshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional
    public ProductResponseDTO create(ProductDTO dto) {
        log.info("Creating new product: {}", dto.getName());

        if (productRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            log.warn("Attempt to create duplicate product: {}", dto.getName());
            throw new IllegalArgumentException("Product already exists");
        }

        Product product = productMapper.dtoToEntity(dto);
        Product saved = productRepository.save(product);
        log.info("Product created successfully with ID: {}", saved.getId());

        return productMapper.entityToResponseDto(saved);
    }

    public ProductResponseDTO findById(Long id) {
        log.info("Finding product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found: {}", id);
                    return new ProductNotFoundException(id);
                });

        return productMapper.entityToResponseDto(product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductDTO dto) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for update: {}", id);
                    return new ProductNotFoundException(id);
                });

        // Validate that the new name doesn't belong to a different product
        if (dto.getName() != null && !dto.getName().isBlank()) {
            productRepository.findByNameIgnoreCase(dto.getName())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        log.warn("Duplicate product name on update: {}", dto.getName());
                        throw new IllegalArgumentException("Product name already exists");
                    });
        }

        productMapper.updateEntityFromDto(dto, product);

        Product updated = productRepository.save(product);
        log.info("Product updated successfully: {}", id);

        return productMapper.entityToResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for delete: {}", id);
                    return new ProductNotFoundException(id);
                });

        product.setActive(false);
        productRepository.save(product);
        log.info("Soft deleted product: {}", id);
    }

    public List<ProductResponseDTO> findAllActive() {
        log.info("Finding all active products");
        List<Product> products = productRepository.findByActiveTrue();
        log.info("Found {} active products", products.size());
        return productMapper.entityListToResponseDtoList(products);
    }

    public Page<ProductResponseDTO> findAllActive(Pageable pageable) {
        log.info("Finding active products with pagination: page {}, size {}",
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productsPage = productRepository.findByActiveTrue(pageable);
        log.info("Found {} active products on page {}",
                 productsPage.getNumberOfElements(), productsPage.getNumber());
        return productsPage.map(productMapper::entityToResponseDto);
    }

    public List<ProductResponseDTO> findByPriceRange(BigDecimal priceMin, BigDecimal priceMax) {
        log.info("Finding products with price between {} and {}", priceMin, priceMax);

        if (priceMin.compareTo(priceMax) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        List<Product> products = productRepository.findByPriceBetween(priceMin, priceMax);
        log.info("Found {} products in price range", products.size());
        return productMapper.entityListToResponseDtoList(products);
    }

    public List<ProductResponseDTO> searchActiveProducts(String searchTerm) {
        log.info("Searching active products with term: {}", searchTerm);

        if (searchTerm == null || searchTerm.isBlank()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }

        String pattern = "%" + searchTerm.trim() + "%";
        List<Product> products = productRepository.findActiveProductsByName(pattern);
        log.info("Found {} active products matching '{}'", products.size(), searchTerm);
        return productMapper.entityListToResponseDtoList(products);
    }

    public List<ProductResponseDTO> findAll() {
        log.info("Finding all products");
        List<Product> products = productRepository.findAll();
        log.info("Found {} total products", products.size());
        return productMapper.entityListToResponseDtoList(products);
    }
}
