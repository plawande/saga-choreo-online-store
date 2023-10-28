package com.sample.stock.event.listener;

import com.sample.stock.event.type.OrderCanceledEvent;
import com.sample.stock.event.type.OrderDoneEvent;
import com.sample.stock.event.type.RefundPaymentEvent;
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
public class StockEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.order-done}")
    private String orderDoneTopic;

    @Value("${topic.order-canceled}")
    private String orderCanceledTopic;

    @Value("${topic.refund-payment}")
    private String refundPaymentTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderDoneEvent(OrderDoneEvent event) {
        log.debug("Sending order done event to {}, event: {}", orderDoneTopic, event);
        kafkaTemplate.send(orderDoneTopic, event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onOrderCanceledEvent(OrderCanceledEvent event) {
        log.debug("Sending order canceled event to {}, event: {} ", orderCanceledTopic, event);
        kafkaTemplate.send(orderCanceledTopic, event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRefundPaymentEvent(RefundPaymentEvent event) {
        log.debug("Sending refund payment event to {}, event: {} ", refundPaymentTopic, event);
        kafkaTemplate.send(refundPaymentTopic, event);
    }
}