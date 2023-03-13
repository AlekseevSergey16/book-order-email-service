package com.salekseev.emailservice.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salekseev.emailservice.model.OrderCreatedMessage;
import com.salekseev.emailservice.service.MailService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitConsumer.class);

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    public RabbitConsumer(MailService mailService, ObjectMapper objectMapper) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @RabbitListener(queues = "${queue-name}")
    public void processMyQueue(String message) {
        LOG.info("Received from myQueue : {} ", new String(message.getBytes()));

        OrderCreatedMessage orderMessage = objectMapper.readValue(message, OrderCreatedMessage.class);

        mailService.sendOrderMessage(orderMessage.getUserEmail(), "Заказ № " + orderMessage.getOrderId(), orderMessage);
    }

}
