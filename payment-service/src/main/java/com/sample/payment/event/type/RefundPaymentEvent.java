package com.sample.payment.event.type;

import com.sample.payment.model.Order;
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