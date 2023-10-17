package com.sample.order.event.handler;

import com.sample.order.event.type.OrderCanceledEvent;
import com.sample.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderCanceledEventHandler {

    private OrderService orderService;

    @RabbitListener(queues = "${queue.order-cancel}")
    public void handle(OrderCanceledEvent event) {
        log.debug("Handling a order canceled event {}", event);
        orderService.cancelOrder(event.getOrder().getId());
    }
}