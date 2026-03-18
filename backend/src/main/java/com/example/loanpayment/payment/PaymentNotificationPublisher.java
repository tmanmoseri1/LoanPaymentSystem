package com.example.loanpayment.payment;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Optional: publishes a message to RabbitMQ after a successful payment.
 * If RabbitMQ is not running, the publisher is not used (guarded in PaymentService).
 */
@Component
public class PaymentNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public PaymentNotificationPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbit.exchange:payments.exchange}") String exchange,
            @Value("${app.rabbit.routingKey:payments.created}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publishPaymentCreated(String payload) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
