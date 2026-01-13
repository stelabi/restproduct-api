package org.restshop.mapper;


import org.restshop.dto.ProductDTO;
import org.restshop.entity.Product;
import org.springframework.stereotype.Component;

@Component
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
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPhone(product.getPhone());
        dto.setContact(product.getContact());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setActive(product.isActive());

        return dto;
    }
}
