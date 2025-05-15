package com.vlz.authservice.kafkaGetaway;

import com.vlz.authservice.dto.event.UserAddEvent;
import com.vlz.authservice.dto.event.UserSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationKafkaGateway {

    private final ReplyingKafkaTemplate<String, Object, Object> userReplyingKafkaTemplate;

    @Value("${spring.kafka.reply.timeout}")
    private long replyTimeoutSeconds;

    @Value("${spring.kafka.topic.names.user-add-request-topic}")
    private String userAddRequestTopicName;

    public UserSavedEvent sendRegistrationRequest(UserAddEvent userAddEvent) {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(userAddRequestTopicName, userAddEvent);
            RequestReplyFuture<String, Object, Object> replyFuture =
                    userReplyingKafkaTemplate.sendAndReceive(record);

            ConsumerRecord<String, Object> replyRecord = replyFuture.get(
                    replyTimeoutSeconds,
                    TimeUnit.SECONDS
            );

            Object replyValue = replyRecord.value();

            if (replyValue instanceof UserSavedEvent) {
                return (UserSavedEvent) replyValue;
            } else {
                log.error("Получен неожиданный тип ответа или ошибка при регистрации пользователя: {}", replyValue);
                throw new RuntimeException("Ошибка регистрации пользователя или получен неожиданный ответ от Kafka сервиса.");
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Ошибка во время Kafka запрос-ответ при регистрации пользователя: {}", e.getMessage());
            throw new RuntimeException("Не удалось завершить регистрацию пользователя через Kafka запрос-ответ.", e);
        }
    }
}

