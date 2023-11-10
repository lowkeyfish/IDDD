package com.yujunyang.iddd.dealer.config.rabbitmq;

import com.yujunyang.iddd.common.utils.JacksonUtils;
import com.yujunyang.iddd.dealer.domain.order.OrderType;
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
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "iddd.topic";

    public final String internalDealerQueueName = "iddd_internal_dealer";
    public final String internalDealerQueueRoutingKey = "Dealer.#";

    public final String internalDealerServicePurchaseOrderQueueName = "iddd_internal_dealer_service_purchase_order";
    public final String internalDealerServicePurchaseOrderQueueOrderRoutingKey =
            "Order." + OrderType.DEALER_SERVICE_PURCHASE_ORDER.routingKeySegment() + ".#";
    public final String internalDealerServicePurchaseOrderQueuePaymentRoutingKey =
            "Payment." + OrderType.DEALER_SERVICE_PURCHASE_ORDER.routingKeySegment() + ".#";

    public final String internalPaymentQueueName = "iddd_internal_payment";
    public final String internalPaymentQueueRoutingKey = "Payment.#";


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
        Queue queue = new Queue(internalDealerQueueName, true);
        queue.setShouldDeclare(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Binding internalDealerQueueBinding(RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.
                bind(internalDealerQueue(rabbitAdmin)).
                to(new TopicExchange(EXCHANGE_NAME)).with(internalDealerQueueRoutingKey);
        binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }

    @Bean
    public Queue internalDealerServicePurchaseOrderQueue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(internalDealerServicePurchaseOrderQueueName, true);
        queue.setShouldDeclare(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Binding internalDealerServicePurchaseOrderQueueOrderBinding(RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.
                bind(internalDealerServicePurchaseOrderQueue(rabbitAdmin)).
                to(new TopicExchange(EXCHANGE_NAME)).with(internalDealerServicePurchaseOrderQueueOrderRoutingKey);
        binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }

    @Bean
    public Binding internalDealerServicePurchaseOrderQueuePaymentBinding(RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.
                bind(internalDealerServicePurchaseOrderQueue(rabbitAdmin)).
                to(new TopicExchange(EXCHANGE_NAME)).with(internalDealerServicePurchaseOrderQueuePaymentRoutingKey);
        binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }
}
