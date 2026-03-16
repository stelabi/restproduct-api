package org.restshop.mapper;


import lombok.extern.slf4j.Slf4j;
import org.restshop.dto.ProductDTO;
import org.restshop.dto.ProductResponseDTO;
import org.restshop.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProductMapper {

    // DTO -> Entity
    public Product dtoToEntity(ProductDTO dto){
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setContact(dto.getContact());
        product.setPhone(dto.getPhone());
        product.setDescription(dto.getDescription());
        product.setActive(dto.isActive());

        return product;
    }

    // Entity -> DTO
    public ProductDTO entityToDto(Product product){
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPhone(product.getPhone());
        dto.setContact(product.getContact());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setActive(product.isActive());

        return dto;
    }

    // List DTO -> List Entity
    public List<Product> dtoListToEntityList(List<ProductDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            log.debug("Empty or null ProductDTO list provided");
            return new ArrayList<>();
        }

        return dtoList.stream()
                .map(this::dtoToEntity)
                .collect(Collectors.toList());
    }

    // List Entity -> List DTO
    public List<ProductDTO> entityListToDtoList(List<Product> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            log.debug("Empty or null Product entity list provided");
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // Entity -> ResponseDTO
    public ProductResponseDTO entityToResponseDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPhone(product.getPhone());
        dto.setContact(product.getContact());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setActive(product.isActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

    // List Entity -> List ResponseDTO
    public List<ProductResponseDTO> entityListToResponseDtoList(List<Product> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            log.debug("Empty or null Product entity list provided");
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(this::entityToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing Product Entity with data from ProductDTO
     * Only updates non-null fields
     */
    public void updateEntityFromDto(ProductDTO dto, Product entity) {
        if (dto == null || entity == null) {
            log.warn("Attempt to update with null values");
            return;
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            entity.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getQuantity() != null) {
            entity.setQuantity(dto.getQuantity());
        }
        if (dto.getContact() != null && !dto.getContact().isBlank()) {
            entity.setContact(dto.getContact());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            entity.setDescription(dto.getDescription());
        }

        log.debug("Product entity updated from DTO successfully");
    }
}
