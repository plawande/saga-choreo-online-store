package com.sample.order.event.handler;

import com.sample.common.event.type.OrderDoneEvent;
import com.sample.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderDoneEventHandler {

    private OrderService orderService;

    @KafkaListener(topics = "${topic.order-done}", groupId = "${group.order-done}")
    public void handle(OrderDoneEvent event) {
        log.debug("Handling a order done event {}", event);
        orderService.updateOrderAsDone(event.getOrder().getId());
    }
}
