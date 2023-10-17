package com.sample.payment.event.handler;

import com.sample.payment.event.type.RefundPaymentEvent;
import com.sample.payment.service.PaymentService;
import com.sample.payment.util.TransactionIdHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class RefundPaymentEventHandler {

    private PaymentService paymentService;
    private TransactionIdHolder transactionIdHolder;

    @RabbitListener(queues = "${queue.refund-payment}")
    public void handle(RefundPaymentEvent event) {
        transactionIdHolder.setCurrentTransactionId(event.getTransactionId());
        paymentService.refund(event.getOrder().getId());
    }
}
