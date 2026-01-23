package org.restshop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restshop.dto.ProductDTO;
import org.restshop.entity.Product;
import org.restshop.mapper.ProductMapper;
import org.restshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional
    public ProductDTO create(ProductDTO dto) {
        log.info("Creating new product: {}", dto.getName());

        // Validate duplicate data
        if (productRepository.findByName(dto.getName()).isPresent()) {
            log.warn("Attempt to create duplicate product: {}", dto.getName());
            throw new IllegalArgumentException("Product already exists");
        }

        // Map DTO to Entity
        Product product = productMapper.dtoToEntity(dto);

        // Save to database
        Product saved = productRepository.save(product);
        log.info("Product created successfully with ID: {}", saved.getId());

        // Return DTO
        return productMapper.entityToDto(saved);

    }

    public ProductDTO findById(Long id) {
        log.info("Finding product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found: {}", id);
                    return new EntityNotFoundException("Product not found");
                });

        return productMapper.entityToDto(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for update: {}", id);
                    return new EntityNotFoundException("Product not found");
                });

        // Use partial mapping for updates
        productMapper.updateEntityFromDto(dto, product);

        Product updated = productRepository.save(product);
        log.info("Product updated successfully: {}", id);

        return productMapper.entityToDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }

        productRepository.deleteById(id);
        log.info("Delete product: {}", id);
    }

}
