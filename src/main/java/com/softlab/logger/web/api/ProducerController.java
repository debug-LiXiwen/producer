package com.softlab.logger.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softlab.logger.common.util.JsonUtil;
import com.softlab.logger.core.model.vo.LogVo;
import com.softlab.logger.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiXiwen on 2019/4/2 16:19.
 **/

/**
 * password:178415
 * 日志打印部分，实现日志管理
 * 总体上使用 rabbitmq 实现消息队列机制，接收消息，即日志，把日志转为json格式，json序列化和反序列化
 * api：
 *
 * 一 level 级别 ：1.warn  2.info  3.error   4.debug
 *
 * 二 application 应用层级别，放到tomcat中，每个项目只有一个名字。
 *
 * 三 tag 标签  例子：2019-03-29 16:07:34.385  INFO 3536 --- [nio-9999-exec-2] com.softlab.wx.web.api.WxStepController  : doGet Over
 *             分号前面一部分为标签
 *
 * 四 timestamp  时间，24小时 ，时分秒机制，显示每个日志的打印时间
 *
 * 五 内容  ：实现模糊查询  ，多条件模糊查询，内容为分号后面的一部分
 */

@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
public class ProducerController {
    private static final Logger log= LoggerFactory.getLogger(ProducerController.class);

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final Environment env;
    private final ProducerService producerService;

    @Autowired
    public ProducerController(ObjectMapper objectMapper , RabbitTemplate rabbitTemplate , Environment env , ProducerService producerService){
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.env = env;
        this.producerService = producerService;
    }

    /**
     * 发送对象消息
     */

    @RequestMapping(value = "/send",method = RequestMethod.POST)
    public Map<String, Object> sendObjectMessage(@RequestBody LogVo logVo){
        log.info("发送对象消息 : logVo = "+ JsonUtil.getJsonString(logVo));

        Map<String,Object> map = new HashMap<>();
        try {
            map = producerService.sendLog(logVo);
            return map;
        }catch (Exception e){
            log.error("发送对象消息发生异常： ",e.fillInStackTrace());
            map.put("code",1);
            map.put("message",e.getMessage());
            return map;
        }
    }




}
