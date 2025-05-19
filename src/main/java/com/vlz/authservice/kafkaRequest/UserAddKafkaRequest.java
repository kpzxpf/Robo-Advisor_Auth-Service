package com.vlz.authservice.kafkaRequest;

import com.vlz.authservice.dto.event.UserAddEvent;
import com.vlz.authservice.dto.event.UserSavedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class UserAddKafkaRequest {
    private final ReplyingKafkaTemplate<String, Object, Object> userAddReplyingKafkaTemplate;

    @Value("${spring.kafka.topic.names.user-add-request-topic}")
    private String userAddRequestTopicName;

    @Value("${spring.kafka.reply.timeout}")
    private long replyTimeoutSeconds;


    public UserAddKafkaRequest(@Qualifier("userAddReplyingKafkaTemplate")
                               ReplyingKafkaTemplate<String, Object, Object> userAddReplyingKafkaTemplate) {

        this.userAddReplyingKafkaTemplate = userAddReplyingKafkaTemplate;
    }

    public UserSavedEvent sendRegistrationRequest(UserAddEvent userAddEvent) {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(userAddRequestTopicName, userAddEvent);
            RequestReplyFuture<String, Object, Object> replyFuture = userAddReplyingKafkaTemplate.sendAndReceive(record);
            ConsumerRecord<String, Object> replyRecord = replyFuture.get(replyTimeoutSeconds, TimeUnit.SECONDS);
            Object replyValue = replyRecord.value();

            if (replyValue instanceof UserSavedEvent event) {
                log.info("Received UserSavedEvent reply from topic {}: {}", userAddRequestTopicName, event);
                if (!event.isSuccess()) {
                    throw new RuntimeException("User registration failed: " + event.getMessage());
                }
                return event;
            } else {
                log.error("Unexpected reply type received from topic {}: {}", userAddRequestTopicName, replyValue);
                throw new RuntimeException("Unexpected response type received from Kafka service.");
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error during Kafka request to topic {}: {}", userAddRequestTopicName, e.getMessage());
            throw new RuntimeException("Failed to complete user registration via Kafka.", e);
        }
    }
}