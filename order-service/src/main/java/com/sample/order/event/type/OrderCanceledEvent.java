package com.sample.order.event.type;

import com.sample.order.entity.Order;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderCanceledEvent {
    private String transactionId;
    private Order order;
}