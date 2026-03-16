package org.restshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restshop.dto.ProductDTO;
import org.restshop.dto.ProductResponseDTO;
import org.restshop.entity.Product;
import org.restshop.exception.ProductNotFoundException;
import org.restshop.mapper.ProductMapper;
import org.restshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    // --- create ---

    @Test
    void create_success() {
        ProductDTO dto = buildDto("Laptop");
        Product entity = buildProduct(1L, "Laptop");
        ProductResponseDTO response = buildResponse(1L, "Laptop");

        when(productRepository.findByNameIgnoreCase("Laptop")).thenReturn(Optional.empty());
        when(productMapper.dtoToEntity(dto)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.entityToResponseDto(entity)).thenReturn(response);

        ProductResponseDTO result = productService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository).save(entity);
    }

    @Test
    void create_throwsOnDuplicate() {
        ProductDTO dto = buildDto("Laptop");
        when(productRepository.findByNameIgnoreCase("Laptop")).thenReturn(Optional.of(new Product()));

        assertThatThrownBy(() -> productService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    // --- findById ---

    @Test
    void findById_success() {
        Product entity = buildProduct(1L, "Laptop");
        ProductResponseDTO response = buildResponse(1L, "Laptop");

        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(productMapper.entityToResponseDto(entity)).thenReturn(response);

        ProductResponseDTO result = productService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- update ---

    @Test
    void update_success() {
        ProductDTO dto = buildDto("Laptop Pro");
        Product entity = buildProduct(1L, "Laptop");
        ProductResponseDTO response = buildResponse(1L, "Laptop Pro");

        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(productRepository.findByNameIgnoreCase("Laptop Pro")).thenReturn(Optional.empty());
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.entityToResponseDto(entity)).thenReturn(response);

        ProductResponseDTO result = productService.update(1L, dto);

        assertThat(result.getName()).isEqualTo("Laptop Pro");
        verify(productMapper).updateEntityFromDto(dto, entity);
    }

    @Test
    void update_throwsOnDuplicateName() {
        ProductDTO dto = buildDto("Existing");
        Product currentProduct = buildProduct(1L, "Laptop");
        Product otherProduct = buildProduct(2L, "Existing");

        when(productRepository.findById(1L)).thenReturn(Optional.of(currentProduct));
        when(productRepository.findByNameIgnoreCase("Existing")).thenReturn(Optional.of(otherProduct));

        assertThatThrownBy(() -> productService.update(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void update_throwsWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, buildDto("X")))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_softDelete_success() {
        Product entity = buildProduct(1L, "Laptop");
        entity.setActive(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));

        productService.delete(1L);

        assertThat(entity.isActive()).isFalse();
        verify(productRepository).save(entity);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- findByPriceRange ---

    @Test
    void findByPriceRange_throwsWhenMinGreaterThanMax() {
        assertThatThrownBy(() -> productService.findByPriceRange(
                new BigDecimal("100.00"), new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Minimum price");
    }

    // --- searchActiveProducts ---

    @Test
    void searchActiveProducts_throwsOnBlankTerm() {
        assertThatThrownBy(() -> productService.searchActiveProducts("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Search term");

        assertThatThrownBy(() -> productService.searchActiveProducts(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- helpers ---

    private ProductDTO buildDto(String name) {
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setPrice(new BigDecimal("9.99"));
        dto.setQuantity(10);
        dto.setDescription("A description");
        dto.setActive(true);
        return dto;
    }

    private Product buildProduct(Long id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(new BigDecimal("9.99"));
        p.setQuantity(10);
        p.setActive(true);
        return p;
    }

    private ProductResponseDTO buildResponse(Long id, String name) {
        ProductResponseDTO r = new ProductResponseDTO();
        r.setId(id);
        r.setName(name);
        return r;
    }
}
