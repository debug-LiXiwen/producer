package com.softlab.logger.common;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * Created by LiXiwen on 2019/4/1 20:49.
 **/

@Configuration
public class RabbitConfig {

   /* private static final String QUEUE_NAME = "direct_queue";
    @Bean
    public Queue Queue() {
        return new Queue(QUEUE_NAME,true,false,false, new HashMap<>());
    }*/

}