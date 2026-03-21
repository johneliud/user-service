package io.github.johneliud.user_service.services;

import io.github.johneliud.user_service.dto.SellerStatsResponse;
import io.github.johneliud.user_service.dto.UserStatsResponse;
import io.github.johneliud.user_service.models.ProductStat;
import io.github.johneliud.user_service.models.Role;
import io.github.johneliud.user_service.models.User;
import io.github.johneliud.user_service.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceStatsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserService userService;

    private User userWithStats(String id, Role role, BigDecimal totalSpent, BigDecimal totalRevenue,
                               List<ProductStat> stats) {
        User user = new User();
        user.setId(id);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("hashed");
        user.setRole(role);
        user.setTotalSpent(totalSpent);
        user.setTotalRevenue(totalRevenue);
        user.setProductStats(new ArrayList<>(stats));
        return user;
    }

    // ── getUserStats ─────────────────────────────────────────────────────────

    @Test
    void getUserStats_returnsTopProductsSortedByQty() {
        ProductStat lowQty = new ProductStat("p1", "Phone", 3, new BigDecimal("30.00"));
        ProductStat highQty = new ProductStat("p2", "Laptop", 5, new BigDecimal("50.00"));
        User user = userWithStats("u1", Role.CLIENT, new BigDecimal("80.00"), BigDecimal.ZERO,
                List.of(lowQty, highQty));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        UserStatsResponse result = userService.getUserStats("u1");

        assertThat(result.getTotalSpent()).isEqualByComparingTo("80.00");
        assertThat(result.getTopProducts()).hasSize(2);
        assertThat(result.getTopProducts().get(0).getProductId()).isEqualTo("p2");
        assertThat(result.getTopProducts().get(1).getProductId()).isEqualTo("p1");
    }

    @Test
    void getUserStats_noHistory_returnsZeroTotals() {
        User user = userWithStats("u1", Role.CLIENT, BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        UserStatsResponse result = userService.getUserStats("u1");

        assertThat(result.getTotalSpent()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTopProducts()).isEmpty();
    }

    @Test
    void getUserStats_userNotFound_throws() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserStats("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    // ── getSellerStats ───────────────────────────────────────────────────────

    @Test
    void getSellerStats_returnsTopProductsSortedByAmount() {
        ProductStat lowRevenue = new ProductStat("p1", "Shirt", 4, new BigDecimal("40.00"));
        ProductStat highRevenue = new ProductStat("p2", "Jacket", 2, new BigDecimal("200.00"));
        User seller = userWithStats("s1", Role.SELLER, BigDecimal.ZERO, new BigDecimal("240.00"),
                List.of(lowRevenue, highRevenue));
        when(userRepository.findById("s1")).thenReturn(Optional.of(seller));

        SellerStatsResponse result = userService.getSellerStats("s1");

        assertThat(result.getTotalRevenue()).isEqualByComparingTo("240.00");
        assertThat(result.getTopProducts()).hasSize(2);
        assertThat(result.getTopProducts().get(0).getProductId()).isEqualTo("p2");
        assertThat(result.getTopProducts().get(1).getProductId()).isEqualTo("p1");
    }

    @Test
    void getSellerStats_userNotFound_throws() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getSellerStats("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}
