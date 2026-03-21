package io.github.johneliud.user_service.dto;

import io.github.johneliud.user_service.models.ProductStat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatsResponse {
    private BigDecimal totalRevenue;
    private List<ProductStat> topProducts;
}
