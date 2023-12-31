package com.sample.order.service;

import com.sample.order.entity.Order;
import com.sample.common.event.type.OrderCreatedEvent;
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
        com.sample.common.model.Order orderDto = mapEntityToDto(order);
        OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID().toString(), orderDto);
        log.debug("Publishing an order created event {}", event);
        publisher.publishEvent(event);
    }

    private com.sample.common.model.Order mapEntityToDto(Order order) {
        com.sample.common.model.Order orderDto = new com.sample.common.model.Order();
        orderDto.setId(order.getId());
        orderDto.setProductId(order.getProductId());
        orderDto.setValue(order.getValue());
        orderDto.setQuantity(order.getQuantity());
        return orderDto;
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
