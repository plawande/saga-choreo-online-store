package com.sample.payment.event.type;

import com.sample.payment.model.Order;
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