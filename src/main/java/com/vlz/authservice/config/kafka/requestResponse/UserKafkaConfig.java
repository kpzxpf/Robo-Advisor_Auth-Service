package com.vlz.authservice.config.kafka.requestResponse;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class UserKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.topic.partitions}")
    private int partitions;
    @Value("${spring.kafka.topic.replicationFactor}")
    private short replicationFactor;
    @Value("${spring.kafka.reply.timeout}")
    private long replyTimeoutSeconds;


    @Value("${spring.kafka.topic.names.user-add-request-topic}")
    private String userAddRequestTopicName;
    @Value("${spring.kafka.topic.names.user-add-reply-topic}")
    private String userAddReplyTopicName;

    @Bean
    public NewTopic userAddRequestTopic() {
        return new NewTopic(userAddRequestTopicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic userAddReplyTopic() {
        return new NewTopic(userAddReplyTopicName, partitions, replicationFactor);
    }

    @Bean
    public ConsumerFactory<String, Object> userReplyConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-add-reply-group");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> userReplyKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userReplyConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> userReplyMessageContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> userReplyKafkaListenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, Object> container =
                userReplyKafkaListenerContainerFactory.createContainer(userAddReplyTopicName);
        container.getContainerProperties().setMissingTopicsFatal(false);
        return container;
    }

    @Bean
    public ProducerFactory<String, Object> userRequestProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, Object> userReplyingKafkaTemplate(
            ProducerFactory<String, Object> userRequestProducerFactory,
            ConcurrentMessageListenerContainer<String, Object> userReplyMessageContainer) {
        ReplyingKafkaTemplate<String, Object, Object> template =
                new ReplyingKafkaTemplate<>(userRequestProducerFactory, userReplyMessageContainer);
        template.setDefaultReplyTimeout(Duration.ofSeconds(replyTimeoutSeconds));
        return template;
    }

}