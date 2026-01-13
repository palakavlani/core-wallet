package io.ledger.wallet.rabbitmq.consumer;

import io.ledger.wallet.api.dto.AuditLogMessage;
import io.ledger.wallet.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j 
public class AuditConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeAuditLog(AuditLogMessage message) {

        log.info("=============== AUDIT LOG RECEIVED ===============");
        log.info("Sender: {}", message.getSender());
        log.info("Receiver: {}", message.getReceiver());
        log.info("Amount: ${}", message.getAmount());
        log.info("Timestamp: {}", message.getTimestamp());
        log.info("==================================================");

    }
}
