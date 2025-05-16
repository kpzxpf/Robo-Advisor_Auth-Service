package com.vlz.authservice.kafkaGetaway;

import com.vlz.authservice.dto.event.FindUserByUsernameRequest;
import com.vlz.authservice.entity.User;
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
public class UserFindByUsernameKafkaGateway {
    private final ReplyingKafkaTemplate<String, Object, Object> userFindByIdReplyingKafkaTemplate;

    @Value("${spring.kafka.topic.names.user-find-by-name-request-topic}")
    private String userFindByIdRequestTopicName;

    @Value("${spring.kafka.reply.timeout}")
    private long replyTimeoutSeconds;

    public UserFindByUsernameKafkaGateway(@Qualifier("userFindByIdReplyingKafkaTemplate")
                                          ReplyingKafkaTemplate<String, Object, Object> userFindByIdReplyingKafkaTemplate) {

        this.userFindByIdReplyingKafkaTemplate = userFindByIdReplyingKafkaTemplate;
    }


    public User findUserByIdRequest(FindUserByUsernameRequest findUserByUsernameRequest) {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(userFindByIdRequestTopicName, findUserByUsernameRequest);

            RequestReplyFuture<String, Object, Object> replyFuture =
                    userFindByIdReplyingKafkaTemplate.sendAndReceive(record);

            ConsumerRecord<String, Object> replyRecord = replyFuture.get(
                    replyTimeoutSeconds,
                    TimeUnit.SECONDS
            );

            Object replyValue = replyRecord.value();

            if (replyValue instanceof User) {
                log.info("Received User reply for find by ID: {}", replyValue);
                return (User) replyValue;
            } else if (replyValue == null) {
                log.info("User not found for ID request: {}", findUserByUsernameRequest.getUsername());
                return null;
            } else {
                log.error("Received unexpected reply type or error for user find by ID: {}", replyValue);
                throw new RuntimeException("User find by ID failed or received unexpected reply from Kafka service.");
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error during Kafka request-response for user find by ID: {}", e.getMessage());
            throw new RuntimeException("Failed to find user by ID via Kafka request-response.", e);
        }
    }
}