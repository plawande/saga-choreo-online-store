package com.sample.order.event.handler;

import com.sample.order.event.type.OrderDoneEvent;
import com.sample.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderDoneEventHandler {

    private OrderService orderService;

    @RabbitListener(queues = "${queue.order-done}")
    public void handle(OrderDoneEvent event) {
        log.debug("Handling a order done event {}", event);
        orderService.updateOrderAsDone(event.getOrder().getId());
    }
}
