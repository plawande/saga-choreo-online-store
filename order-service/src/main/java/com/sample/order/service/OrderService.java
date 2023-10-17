package com.sample.order.service;

import com.sample.order.entity.Order;
import com.sample.order.event.type.OrderCreatedEvent;
import com.sample.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ApplicationEventPublisher publisher;

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(Order.OrderStatus.NEW);
        log.debug("Saving an order {}", order);
        orderRepository.save(order);
        publishOrderCreated(order);
        return order;
    }

    private void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID().toString(), order);
        log.debug("Publishing an order created event {}", event);
        publisher.publishEvent(event);
    }

    @Transactional
    public void updateOrderAsDone(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(Order.OrderStatus.DONE);
            log.debug("Order {} done", order.getId());
        }else {
            log.error("Cannot update Order to status {}, Order {} not found", Order.OrderStatus.DONE, orderId);
        }
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(Order.OrderStatus.CANCELED);
            log.debug("Order {} canceled", order.getId());
        }else {
            log.error("Cannot update Order to status {}, Order {} not found", Order.OrderStatus.CANCELED, orderId);
        }
    }
}
