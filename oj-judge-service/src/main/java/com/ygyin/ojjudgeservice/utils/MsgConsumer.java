package com.ygyin.ojjudgeservice.utils;

import com.rabbitmq.client.Channel;
import com.ygyin.ojjudgeservice.judge.JudgeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class MsgConsumer {

    @Resource
    private JudgeService judgeService;

    // 指定监听的队列
    @SneakyThrows
    @RabbitListener(queues = {"my_queue1"}, ackMode = "MANUAL")
    public void consumeMsg(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        // 将异步调用判题服务操作放到消息消费者里
        log.info("Consumer receives Msg = {}", msg);
        long problemSubmitId = Long.parseLong(msg);

        try {
            judgeService.doJudgeProblem(problemSubmitId);
            channel.basicAck(tag, false);
        } catch (IOException e) {
            channel.basicNack(tag, false, false);
        }
    }

}
