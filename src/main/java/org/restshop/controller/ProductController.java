package org.restshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restshop.dto.ProductDTO;
import org.restshop.dto.ProductResponseDTO;
import org.restshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("products")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("name", "price", "quantity", "active", "createdAt", "updatedAt");

    private final ProductService productService;

    @Operation(summary = "Create a new product")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Product already exists")
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductDTO dto) {
        log.info("Creating product");
        ProductResponseDTO result = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        log.info("Getting product by ID: {}", id);
        ProductResponseDTO result = productService.findById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product name already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {
        log.info("Updating product with ID: {}", id);
        ProductResponseDTO result = productService.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Soft-delete a product by ID (sets active=false)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all products (active and inactive)")
    @ApiResponse(responseCode = "200", description = "List of all products")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        log.info("Getting all products");
        List<ProductResponseDTO> results = productService.findAll();
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get all active products")
    @ApiResponse(responseCode = "200", description = "List of active products")
    @GetMapping("/active")
    public ResponseEntity<List<ProductResponseDTO>> getAllActive() {
        log.info("Getting all active products");
        List<ProductResponseDTO> results = productService.findAllActive();
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get active products with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated active products"),
            @ApiResponse(responseCode = "400", description = "Invalid sort field"),
            @ApiResponse(responseCode = "409", description = "Invalid sort field value")
    })
    @GetMapping("/active/paginated")
    public ResponseEntity<Page<ProductResponseDTO>> getAllActivePaginated(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: name, price, quantity, active, createdAt, updatedAt")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC")
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sortBy field: '" + sortBy + "'. Allowed: " + ALLOWED_SORT_FIELDS);
        }

        log.info("Getting active products paginated: page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProductResponseDTO> results = productService.findAllActive(pageable);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get products by price range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products in range"),
            @ApiResponse(responseCode = "409", description = "Min price greater than max price")
    })
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponseDTO>> getByPriceRange(
            @Parameter(description = "Minimum price (inclusive, min 0.01)")
            @RequestParam @DecimalMin("0.01") BigDecimal min,
            @Parameter(description = "Maximum price (inclusive, min 0.01)")
            @RequestParam @DecimalMin("0.01") BigDecimal max) {
        log.info("Getting products by price range: {} - {}", min, max);
        List<ProductResponseDTO> results = productService.findByPriceRange(min, max);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Search active products by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching products"),
            @ApiResponse(responseCode = "400", description = "Search term is blank")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> search(
            @Parameter(description = "Search term (partial match, case-insensitive)")
            @RequestParam @NotBlank String term) {
        log.info("Searching products with term: {}", term);
        List<ProductResponseDTO> results = productService.searchActiveProducts(term);
        return ResponseEntity.ok(results);
    }
}
