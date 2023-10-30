package com.sample.payment.service;

import com.sample.common.model.Order;
import com.sample.payment.entity.Payment;
import com.sample.common.event.type.BilledOrderEvent;
import com.sample.common.event.type.OrderCanceledEvent;
import com.sample.payment.exception.PaymentException;
import com.sample.payment.repository.PaymentRepository;
import com.sample.payment.util.TransactionIdHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {

    private PaymentRepository paymentRepository;
    private TransactionIdHolder transactionIdHolder;
    private ApplicationEventPublisher publisher;

    @Transactional
    public void charge(Order order) {
        confirmCharge(order);
        log.debug("Charging order {}", order);
        Payment payment = createPayment(order);
        log.debug("Saving payment {}", payment);
        paymentRepository.save(payment);
        publishBilledOrder(order);
    }

    private void confirmCharge(Order order) {
        log.debug("Confirm charge for order id {}", order.getId());
        /**
         * logic to check whether the customer has sufficient balance
         */
        Boolean confirm = true;
        if(confirm) {
            log.debug("Charge confirmed for order id {}", order.getId());
        }else {
            publishOrderCanceled(order);
            throw new PaymentException("No sufficient balance in account for order "+order.getId());
        }
    }

    private Payment createPayment(Order order) {
        return Payment.builder()
                .orderId(order.getId())
                .valueBilled(order.getValue())
                .status(Payment.PaymentStatus.BILLED)
                .build();
    }

    private void publishBilledOrder(Order order) {
        BilledOrderEvent event = new BilledOrderEvent(transactionIdHolder.getCurrentTransactionId(), order);
        log.debug("Publishing billed order event {}", event);
        publisher.publishEvent(event);
    }

    private void publishOrderCanceled(Order order) {
        OrderCanceledEvent event = new OrderCanceledEvent(transactionIdHolder.getCurrentTransactionId(), order);
        log.debug("Publishing order canceled event {}", event);
        publisher.publishEvent(event);
    }

    @Transactional
    public void refund(Long orderId) {
        Optional<Payment> paymentOptional = paymentRepository.findByOrderId(orderId);
        if(paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus(Payment.PaymentStatus.REFUND);
            log.debug("Payment {} refunded", payment.getId());
        }else {
            log.error("Cannot update Payment to status {}, for Order {}", Payment.PaymentStatus.REFUND, orderId);
        }
    }
}
