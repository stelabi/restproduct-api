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

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin("0.01")
    private BigDecimal price;

    @Min(0)
    private Integer quantity;

    @Email
    private String contact;

    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
    private String phone;

    @NotNull
    private String description;

    private boolean active;
}
