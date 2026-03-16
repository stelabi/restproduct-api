package org.restshop.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin("0.01")
    private BigDecimal price;

    @Min(0)
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Email
    private String contact;

    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
    private String phone;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private boolean active;
}
