package com.sample.stock.event.listener;

import com.sample.stock.event.type.OrderCanceledEvent;
import com.sample.stock.event.type.OrderDoneEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class StockEventListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.order-done}")
    private String orderDoneQueue;

    @Value("${exchange.order-canceled}")
    private String orderCanceledExchange;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderDoneEvent(OrderDoneEvent event) {
        log.debug("Sending order done event to {}, event: {}", orderDoneQueue, event);
        rabbitTemplate.convertAndSend(orderDoneQueue, event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onOrderCanceledEvent(OrderCanceledEvent event) {
        log.debug("Sending order canceled event to exchange {}, event: {} ", orderCanceledExchange, event);
        rabbitTemplate.convertAndSend(orderCanceledExchange, "", event);
    }
}

//For sending out order canceled event, fanout exchange is used and hence the routing key is kept blank