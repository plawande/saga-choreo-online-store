package com.sample.stock.event.type;

import com.sample.stock.model.Order;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BilledOrderEvent {
	private String transactionId;
    private Order order;
}