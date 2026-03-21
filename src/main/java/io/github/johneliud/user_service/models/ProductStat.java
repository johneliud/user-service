package io.github.johneliud.user_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStat {
    private String productId;
    private String productName;
    private int totalQuantity;
    private BigDecimal totalAmount;
}
