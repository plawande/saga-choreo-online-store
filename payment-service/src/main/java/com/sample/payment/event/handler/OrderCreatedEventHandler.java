package com.sample.payment.event.handler;

import com.sample.payment.event.type.OrderCreatedEvent;
import com.sample.payment.exception.PaymentException;
import com.sample.payment.service.PaymentService;
import com.sample.payment.util.TransactionIdHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderCreatedEventHandler {

    private TransactionIdHolder transactionIdHolder;
    private PaymentService paymentService;

    @RabbitListener(queues = "${queue.order-create}")
    public void handle(OrderCreatedEvent event) {
        log.debug("Handling a created order event {}", event);
        transactionIdHolder.setCurrentTransactionId(event.getTransactionId());
        try {
            paymentService.charge(event.getOrder());
        }catch(PaymentException e) {
            log.error(e.getMessage());
        }
    }
}
