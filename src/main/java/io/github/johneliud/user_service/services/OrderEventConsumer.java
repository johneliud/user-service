package io.github.johneliud.user_service.services;

import io.github.johneliud.user_service.dto.OrderItemEvent;
import io.github.johneliud.user_service.dto.OrderPlacedEvent;
import io.github.johneliud.user_service.models.ProductStat;
import io.github.johneliud.user_service.models.User;
import io.github.johneliud.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = "order-placed", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order-placed event: orderId={}", event.getOrderId());

        userRepository.findById(event.getUserId()).ifPresent(buyer -> {
            mergeStats(buyer, event.getItems(), event.getTotalAmount(), false);
            userRepository.save(buyer);
            log.info("Updated buyer stats for userId={}", event.getUserId());
        });

        userRepository.findById(event.getSellerId()).ifPresent(seller -> {
            mergeStats(seller, event.getItems(), event.getTotalAmount(), true);
            userRepository.save(seller);
            log.info("Updated seller stats for sellerId={}", event.getSellerId());
        });
    }
}