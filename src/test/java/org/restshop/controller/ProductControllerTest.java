package org.restshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.restshop.dto.ProductDTO;
import org.restshop.dto.ProductResponseDTO;
import org.restshop.exception.GlobalExceptionHandler;
import org.restshop.exception.ProductNotFoundException;
import org.restshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ProductController.class, GlobalExceptionHandler.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_returns201() throws Exception {
        ProductDTO dto = buildValidDto();
        ProductResponseDTO response = buildResponse(1L, "Laptop");

        when(productService.create(any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void createProduct_returns400_whenInvalidBody() throws Exception {
        ProductDTO dto = new ProductDTO(); // missing required fields

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returns200() throws Exception {
        ProductResponseDTO response = buildResponse(1L, "Laptop");
        when(productService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(productService.findById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPaginated_returns400_whenInvalidSortBy() throws Exception {
        when(productService.findAllActive(any())).thenReturn(null);

        mockMvc.perform(get("/products/active/paginated")
                        .param("sortBy", "invalidField"))
                .andExpect(status().isConflict());
    }

    // --- helpers ---

    private ProductDTO buildValidDto() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Laptop");
        dto.setPrice(new BigDecimal("999.99"));
        dto.setQuantity(5);
        dto.setDescription("A powerful laptop");
        dto.setActive(true);
        return dto;
    }

    private ProductResponseDTO buildResponse(Long id, String name) {
        ProductResponseDTO r = new ProductResponseDTO();
        r.setId(id);
        r.setName(name);
        return r;
    }
}
