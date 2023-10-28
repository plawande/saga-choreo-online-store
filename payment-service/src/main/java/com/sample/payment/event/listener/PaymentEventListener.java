package com.sample.payment.event.listener;

import com.sample.payment.event.type.BilledOrderEvent;
import com.sample.payment.event.type.OrderCanceledEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PaymentEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.billed-order}")
    private String billedOrderTopic;

    @Value("${topic.order-cancel}")
    private String orderCanceledTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderBilledEvent(BilledOrderEvent event) {
        log.debug("Sending billed order event {} to queue {}", event, billedOrderTopic);
        kafkaTemplate.send(billedOrderTopic, event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onOrderCanceledEvent(OrderCanceledEvent event) {
        log.debug("Sending order canceled event {} to queue {}", event, orderCanceledTopic);
        kafkaTemplate.send(orderCanceledTopic, event);
    }
}
