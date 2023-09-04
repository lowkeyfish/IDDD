package com.yujunyang.iddd.dealer.config.rabbitmq;

import com.yujunyang.iddd.common.utils.JacksonUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String INTERNAL_DEALER_QUEUE_NAME = "internal_dealer";
    public static final String INTERNAL_DEALER_QUEUE_ROUTING_KEY = "Dealer.#";

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(JacksonUtils.DEFAULT_OBJECT_MAPPER));
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory smartstoreRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer containerFactoryConfigurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(new SimpleMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        containerFactoryConfigurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public Queue internalDealerQueue() {
        Queue queue = new Queue(INTERNAL_DEALER_QUEUE_NAME, true);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public Binding internalDealerQueueBinding() {
        Binding binding = BindingBuilder.
                bind(internalDealerQueue()).
                to(new TopicExchange("amq.topic")).with(INTERNAL_DEALER_QUEUE_ROUTING_KEY);
        binding.setShouldDeclare(true);
        return binding;
    }
}
