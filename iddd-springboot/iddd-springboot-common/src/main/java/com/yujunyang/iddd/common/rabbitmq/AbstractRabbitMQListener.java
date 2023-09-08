package com.yujunyang.iddd.common.rabbitmq;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.yujunyang.iddd.common.exception.ConcurrencyException;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public abstract class AbstractRabbitMQListener {
    protected String queueName;
    protected Logger logger;
    protected List<String> supportMessageTypes;


    protected AbstractRabbitMQListener(
            String queueName,
            List<String> supportMessageTypes,
            Logger logger) {
        this.queueName = queueName;
        this.supportMessageTypes = supportMessageTypes;
        this.logger = logger;
    }

    protected void onMessageWrap(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        boolean requeue = false;

        MessageProperties messageProperties = message.getMessageProperties();
        String type = messageProperties.getHeader("Type");
        String id = messageProperties.getHeader("Id");

        try {
            if (isIgnoreMessageType(type)) {
                logger.warn("{},Listener({})指定忽略此类型消息", key(type, id), this.getClass().getName());
                return;
            }

            if (!isSupportMessageType(type)) {
                logger.error("{},Listener({})不支持处理此消息Type", key(type, id), this.getClass().getName());
                return;
            }

            if (isIgnoreMessageId(id)) {
                logger.warn("{},Listener({})指定忽略此消息", key(type, id), this.getClass().getName());
                return;
            }

            String messageStr = new String(message.getBody(), "utf-8");

            messageHandler(id, type, messageStr);

        } catch (ConcurrencyException e) {
            logger.error("{},消息处理出错,并发操作异常,{}", key(type, id), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("{},消息处理出错:{}", key(type, id), e.getMessage(), e);
            requeue = true;
        } finally {
            try {
                if (requeue) {
                    try {
                        // 重新入队列等待 10s
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    logger.info("{},消息处理出错,消息重新回到队列", key(type, id));
                    channel.basicReject(deliveryTag, true);
                } else {
                    logger.info("{},消息已处理", key(type, id));
                    channel.basicAck(deliveryTag, false);
                }
            } catch (IOException e) {
                logger.error("{},消息处理出错,消息应答出错,{}", key(type, id), e.getMessage(), e);
            }
        }
    }

    protected String key(String type, String id) {
        return MessageFormat.format("队列({0})Type({1})Id({2})", queueName, type, id);
    }

    protected abstract void messageHandler(String id, String type, String body);

    protected boolean isIgnoreMessageId(String id) {
        return false;
    }

    protected boolean isSupportMessageType(String type) {
        return supportMessageTypes.stream().filter(n -> n.equalsIgnoreCase(type)).findFirst().isPresent();
    }

    protected boolean isIgnoreMessageType(String type) {
        return false;
    }
}
