package io.ledger.wallet.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // These constants are the "Address Labels" we will use later
    public static final String QUEUE_NAME = "audit_queue";
    public static final String EXCHANGE_NAME = "wallet_exchange";
    public static final String ROUTING_KEY = "audit.log";

    // 1. Create the Mailbox (Queue)
    @Bean
    public Queue auditQueue() {
        return new Queue(QUEUE_NAME, true); // true = Durable (survives restart)
    }

    // 2. Create the Sorting Center (Exchange)
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 3. Link them together (Binding)
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // 4. The Translator (Objects -> JSON)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 5. The Delivery Truck (Template)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}