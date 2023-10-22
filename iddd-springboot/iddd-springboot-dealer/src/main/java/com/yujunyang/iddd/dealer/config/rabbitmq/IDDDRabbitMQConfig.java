package com.yujunyang.iddd.dealer.config.rabbitmq;

import com.yujunyang.iddd.common.utils.JacksonUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class IDDDRabbitMQConfig {
    public static final String EXCHANGE_NAME = "iddd.topic";

    public final String INTERNAL_DEALER_QUEUE_NAME = "iddd_internal_dealer";
    public final String INTERNAL_DEALER_QUEUE_ROUTING_KEY = "Dealer.#";

    @Value("${rabbitmq.iddd.host}")
    private String host;

    @Value("${rabbitmq.iddd.port}")
    private int port;

    @Value("${rabbitmq.iddd.username}")
    private String username;

    @Value("${rabbitmq.iddd.password}")
    private String password;

    @Value("${rabbitmq.iddd.virtual-host}")
    private String virtualHost;


    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    @Primary
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareExchange(new TopicExchange(EXCHANGE_NAME, true, false));
        return rabbitAdmin;
    }

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(JacksonUtils.DEFAULT_OBJECT_MAPPER));
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    @Primary
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer containerFactoryConfigurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(new SimpleMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        containerFactoryConfigurer.configure(factory, connectionFactory);
        return factory;
    }


    @Bean
    public Queue internalDealerQueue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(INTERNAL_DEALER_QUEUE_NAME, true);
        queue.setShouldDeclare(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Binding internalDealerQueueBinding(RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.
                bind(internalDealerQueue(rabbitAdmin)).
                to(new TopicExchange(EXCHANGE_NAME)).with(INTERNAL_DEALER_QUEUE_ROUTING_KEY);
        binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }
}
