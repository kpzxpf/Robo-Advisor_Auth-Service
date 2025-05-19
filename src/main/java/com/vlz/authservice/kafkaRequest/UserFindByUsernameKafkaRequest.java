package com.vlz.authservice.kafkaRequest;

import com.vlz.authservice.dto.event.FindUserByUsernameReply;
import com.vlz.authservice.dto.event.FindUserByUsernameRequest;
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
public class UserFindByUsernameKafkaRequest {
    private final ReplyingKafkaTemplate<String, Object, Object> userFindByUsernameReplyingKafkaTemplate;

    @Value("${spring.kafka.topic.names.user-find-by-username-request-topic}")
    private String userFindByUsernameRequestTopicName;

    @Value("${spring.kafka.reply.timeout}")
    private long replyTimeoutSeconds;

    public UserFindByUsernameKafkaRequest(
            @Qualifier("userFindByUsernameReplyingKafkaTemplate")
            ReplyingKafkaTemplate<String, Object, Object> userFindByUsernameReplyingKafkaTemplate) {

        this.userFindByUsernameReplyingKafkaTemplate = userFindByUsernameReplyingKafkaTemplate;
    }


    public FindUserByUsernameReply findUserByUsernameRequest(FindUserByUsernameRequest findUserByUsernameRequest) {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(
                    userFindByUsernameRequestTopicName, findUserByUsernameRequest);
            RequestReplyFuture<String, Object, Object> replyFuture =
                    userFindByUsernameReplyingKafkaTemplate.sendAndReceive(record);

            ConsumerRecord<String, Object> replyRecord = replyFuture.get(replyTimeoutSeconds, TimeUnit.SECONDS);
            Object replyValue = replyRecord.value();

            if (replyValue instanceof FindUserByUsernameReply reply) {
                log.info("Received reply from topic {} for username {}: {}",
                        userFindByUsernameRequestTopicName, findUserByUsernameRequest.getUsername(), reply);
                return reply;
            } else {
                log.error("Unexpected reply type received from topic {}: {}",
                        userFindByUsernameRequestTopicName, replyValue);
                throw new RuntimeException("Unexpected response type received from Kafka service.");
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error during Kafka request to topic {} for username {}: {}",
                    userFindByUsernameRequestTopicName, findUserByUsernameRequest.getUsername(), e.getMessage());
            throw new RuntimeException("Failed to find user by username via Kafka.", e);
        }
    }
}