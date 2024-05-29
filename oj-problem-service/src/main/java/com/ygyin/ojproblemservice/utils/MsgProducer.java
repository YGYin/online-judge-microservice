package com.ygyin.ojproblemservice.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MsgProducer {
    @Resource
    private RabbitTemplate template;

    /**
     * 生产者发送消息到交换机
     * @param exchangeId
     * @param routingKey
     * @param msg
     */
    public void sendMsg(String exchangeId, String routingKey, String msg) {
        template.convertAndSend(exchangeId, routingKey, msg);
    }
}
