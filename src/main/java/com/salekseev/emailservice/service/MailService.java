package com.salekseev.emailservice.service;

import com.salekseev.emailservice.model.OrderCreatedMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
public class MailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${from-mail}")
    private String from;

    public MailService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @SneakyThrows
    public void sendOrderMessage(String to, String subject, OrderCreatedMessage orderMessage) {
        Context context = new Context();
        context.setVariable("orderMessage", orderMessage);
        context.setVariable("orderDateTime", orderMessage.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String process = templateEngine.process("mail-template", context);
        javax.mail.internet.MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(subject);
        helper.setText(process, true);
        helper.setTo(to);

        emailSender.send(mimeMessage);
    }

}
