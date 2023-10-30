package com.sample.common.event.type;

import com.sample.common.model.Order;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RefundPaymentEvent {
	private String transactionId;
    private Order order;
}