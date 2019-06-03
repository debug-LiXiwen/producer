package com.softlab.logger.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by LiXiwen on 2019/4/2 16:53.
 **/
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
public class ConsumerController {

    private static final Logger log= LoggerFactory.getLogger(ConsumerController.class);


    @Autowired
    private ObjectMapper objectMapper;



    /**
     * 监听消费用户日志
     * @param message
     */
    @RabbitListener(queues = "${log.user.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeUserLogQueue(@Payload byte[] message){

        try {
            //TODO：接收String
            String result=new String(message,"UTF-8");
            log.info("接收String消息： {} ",result);
        }catch (Exception e){
            log.error("监听消费消息 发生异常： ",e.fillInStackTrace());
        }

    }
}
