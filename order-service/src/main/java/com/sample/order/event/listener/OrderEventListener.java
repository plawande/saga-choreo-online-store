package com.sample.order.event.listener;

import com.sample.common.event.type.OrderCreatedEvent;
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
public class OrderEventListener {

    @Value("${topic.order-create}")
    private String orderCreatedTopic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreateEvent(OrderCreatedEvent event) {
        log.debug("Sending order created event to {}, event: {}", orderCreatedTopic, event);
        kafkaTemplate.send(orderCreatedTopic, event);
    }
}

//@Async makes sure that the Listener event doesn't happen in the same thread as that of @Transactional
