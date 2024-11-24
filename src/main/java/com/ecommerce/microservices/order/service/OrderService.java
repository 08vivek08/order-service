package com.ecommerce.microservices.order.service;

import com.ecommerce.microservices.order.dto.OrderRequest;
import com.ecommerce.microservices.order.model.Order;
import com.ecommerce.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){
        // map OrderRequest to Order object
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .skuCode(orderRequest.skuCode())
                .price(orderRequest.price())
                .quantity(orderRequest.quantity())
                .build();

        // save to OrderRepository
        orderRepository.save(order);
    }
}
