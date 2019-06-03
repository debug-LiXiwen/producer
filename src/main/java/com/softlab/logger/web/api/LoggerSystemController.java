package com.softlab.logger.web.api;

import com.softlab.logger.common.RestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by LiXiwen on 2019/3/28 22:32.
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
public class LoggerSystemController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RestData loggerSystem(){
        return new RestData("暂时未开发");
    }

}
