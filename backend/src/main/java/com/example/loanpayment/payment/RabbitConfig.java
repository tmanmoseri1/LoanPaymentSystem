package com.example.loanpayment.payment;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange paymentsExchange(@Value("${app.rabbit.exchange:payments.exchange}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue paymentsQueue(@Value("${app.rabbit.queue:payments.queue}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding paymentsBinding(Queue paymentsQueue, TopicExchange paymentsExchange,
                                  @Value("${app.rabbit.routingKey:payments.created}") String routingKey) {
        return BindingBuilder.bind(paymentsQueue).to(paymentsExchange).with(routingKey);
    }
}
