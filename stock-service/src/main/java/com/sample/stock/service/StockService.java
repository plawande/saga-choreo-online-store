package com.sample.stock.service;

import com.sample.stock.entity.Product;
import com.sample.stock.event.type.OrderCanceledEvent;
import com.sample.stock.event.type.OrderDoneEvent;
import com.sample.stock.event.type.RefundPaymentEvent;
import com.sample.stock.exception.StockException;
import com.sample.stock.model.Order;
import com.sample.stock.repository.StockRepository;
import com.sample.stock.util.TransactionIdHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class StockService {

    private StockRepository stockRepository;
    private TransactionIdHolder transactionIdHolder;
    private ApplicationEventPublisher publisher;

    @Transactional
    public void updateQuantity(Order order) {
        log.debug("Updating quantity of the product {}", order.getProductId());
        Product product = getProduct(order);
        checkStock(order, product);
        updateStock(order, product);
        publishOrderDone(order);
    }

    private Product getProduct(Order order) {
        Optional<Product> productOptional = stockRepository.findById(order.getProductId());
        return productOptional.orElseThrow(() -> {
            publishOrderCanceled(order);
            return new StockException("Cannot find product " + order.getProductId());
        });
    }

    private void checkStock(Order order, Product product) {
        log.debug("Checking, products available {}, products ordered {}", product.getQuantity(), order.getQuantity());
        if(product.getQuantity() < order.getQuantity()) {
            publishOrderCanceled(order);
            throw new StockException("Insufficient quantity of product "+ order.getProductId());
        }
    }

    private void updateStock(Order order, Product product) {
        Long remainingQuantity = product.getQuantity() - order.getQuantity();
        product.setQuantity(remainingQuantity);
    }

    private void publishOrderDone(Order order) {
        String transactionId = transactionIdHolder.getCurrentTransactionId();
        OrderDoneEvent event = new OrderDoneEvent(transactionId, order);
        log.debug("Publishing order done event {}", event);
        publisher.publishEvent(event);
    }

    private void publishOrderCanceled(Order order) {
        String transactionId = transactionIdHolder.getCurrentTransactionId();
        OrderCanceledEvent event = new OrderCanceledEvent(transactionId, order);
        log.debug("Publishing order canceled event {}", event);
        publisher.publishEvent(event);
        RefundPaymentEvent refundPaymentEvent = new RefundPaymentEvent(transactionId, order);
        log.debug("Publishing refund payment event {}", event);
        publisher.publishEvent(refundPaymentEvent);
    }
}
