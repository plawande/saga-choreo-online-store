package com.sample.common.event.type;

import com.sample.common.model.Order;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDoneEvent {
    private String transactionId;
    private Order order;
}
