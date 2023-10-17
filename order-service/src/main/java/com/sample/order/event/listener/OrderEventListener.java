package com.sample.order.event.listener;

import com.sample.order.event.type.OrderCreatedEvent;
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
public class OrderEventListener {

    @Value("${queue.order-create}")
    private String orderCreatedQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreateEvent(OrderCreatedEvent event) {
        log.debug("Sending order created event to {}, event: {}", orderCreatedQueue, event);
        rabbitTemplate.convertAndSend(orderCreatedQueue, event);
    }
}

//@Async makes sure that the Listener event doesn't happen in the same thread as that of @Transactional
//Here it's using the default direct exchange where queue name is same as routing-key
//https://www.rabbitmq.com/tutorials/amqp-concepts.html#:~:text=The%20default%20exchange%20is%20a,same%20as%20the%20queue%20name.
