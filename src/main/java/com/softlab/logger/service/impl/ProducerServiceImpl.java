package com.softlab.logger.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.softlab.logger.common.ProducerException;
import com.softlab.logger.core.model.vo.LogVo;
import com.softlab.logger.service.ProducerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiXiwen on 2019/6/2 19:41.
 **/
@Service
public class ProducerServiceImpl implements ProducerService {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final Environment env;

    @Autowired
    public ProducerServiceImpl(ObjectMapper objectMapper , RabbitTemplate rabbitTemplate , Environment env ){
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.env = env;
    }

    @Override
    public Map<String, Object> sendLog(LogVo logVo) throws ProducerException {
        Map<String, Object> rtv = new HashMap<>();
        try {
            rabbitTemplate.setExchange(env.getProperty("log.user.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("log.user.routing.key.name"));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(logVo)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON); //发送消息写法二
            rabbitTemplate.convertAndSend(message);
            rtv.put("code",0);
        }catch(Exception e){
            rtv.put("code",1);
            rtv.put("message",e.getMessage());
        }
        return rtv;
    }

}
