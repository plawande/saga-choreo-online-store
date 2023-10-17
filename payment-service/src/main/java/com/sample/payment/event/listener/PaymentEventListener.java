package com.sample.payment.event.listener;

import com.sample.payment.event.type.BilledOrderEvent;
import com.sample.payment.event.type.OrderCanceledEvent;
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
public class PaymentEventListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.billed-order}")
    private String billedOrderQueue;

    @Value("${queue.order-cancel}")
    private String orderCanceledQueue;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderBilledEvent(BilledOrderEvent event) {
        log.debug("Sending billed order event {} to queue {}", event, billedOrderQueue);
        rabbitTemplate.convertAndSend(billedOrderQueue, event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onOrderCanceledEvent(OrderCanceledEvent event) {
        log.debug("Sending order canceled event {} to queue {}", event, orderCanceledQueue);
        rabbitTemplate.convertAndSend(orderCanceledQueue, event);
    }
}
