package com.sample.stock.event.handler;

import com.sample.stock.event.type.BilledOrderEvent;
import com.sample.stock.exception.StockException;
import com.sample.stock.service.StockService;
import com.sample.stock.util.TransactionIdHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class BilledOrderEventHandler {

    private TransactionIdHolder transactionIdHolder;
    private StockService stockService;

    @RabbitListener(queues = "${queue.billed-order}")
    public void handle(BilledOrderEvent event) {
        log.debug("Handling a billed order event {}", event);
        transactionIdHolder.setCurrentTransactionId(event.getTransactionId());
        try {
            stockService.updateQuantity(event.getOrder());
        }catch(StockException e) {
            log.error(e.getMessage());
        }
    }
}
