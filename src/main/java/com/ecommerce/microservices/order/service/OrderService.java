package com.ecommerce.microservices.order.service;

import com.ecommerce.microservices.order.client.InventoryClient;
import com.ecommerce.microservices.order.dto.OrderRequest;
import com.ecommerce.microservices.order.event.OrderPlacedEvent;
import com.ecommerce.microservices.order.model.Order;
import com.ecommerce.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        boolean isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if(isProductInStock) {
            // map OrderRequest to Order object
            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .skuCode(orderRequest.skuCode())
                    .price(orderRequest.price())
                    .quantity(orderRequest.quantity())
                    .build();

            // save to OrderRepository
            orderRepository.save(order);

            // send the message to kafka topic
            log.info("getOrderNumber {} email {}",order.getOrderNumber(), orderRequest.userDetails().email());
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(),
                    orderRequest.userDetails().email(),
                    orderRequest.userDetails().firstName(),
                    orderRequest.userDetails().lastName());
            log.info("Start - Sending OrderPlacedEvent {} to kafka topic order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End - Sending OrderPlacedEvent {} to kafka topic order-placed", orderPlacedEvent);
        }
        else {
            throw new RuntimeException("Product with skuCode " + orderRequest.skuCode() + " is not in Stock");
        }
    }
}
