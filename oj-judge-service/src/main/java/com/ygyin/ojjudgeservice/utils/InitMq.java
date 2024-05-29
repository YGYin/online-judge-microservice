package com.ygyin.ojjudgeservice.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitMq {
    public static void doMqInit() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            // 操作消息队列的客户端
            Channel channel = connection.createChannel();
            // 创建交换机
            String EXCHANGE_ID = "my_exchanger";
            channel.exchangeDeclare(EXCHANGE_ID, "direct");

            String QUEUE_ID = "my_queue1";
            channel.queueDeclare(QUEUE_ID, true, false, false, null);
            channel.queueBind(QUEUE_ID, EXCHANGE_ID, "my_key");

            log.info("MQ init successfully.");
        } catch (Exception e) {
            log.error("MQ init failed.");
        }
    }

    public static void main(String[] args) {
        doMqInit();
    }
}
