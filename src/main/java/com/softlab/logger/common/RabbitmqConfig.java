package com.softlab.logger.common;

import org.springframework.amqp.core.*;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by LiXiwen on 2019/4/1 21:16.
 **/

@Configuration
public class RabbitmqConfig {
    private static final Logger log= LoggerFactory.getLogger(RabbitmqConfig.class);

    private final Environment env;
    private final CachingConnectionFactory connectionFactory;
    private final SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    @Autowired
    public RabbitmqConfig(Environment env,CachingConnectionFactory connectionFactory,SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer){
        this.env = env;
        this.connectionFactory = connectionFactory;
        this.factoryConfigurer = factoryConfigurer;
    }


    /**
     * 单一消费者
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        //自动应答
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    /**
     * 多个消费者
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        /**
         * 这三个参数主要是用于“并发量的配置”
         * 并发消费者的初始化值，并发消费者的最大值，每个消费者每次监听时可拉取处理的消息数量。
         */
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.concurrency",int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.max-concurrency",int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.prefetch",int.class));
        return factory;
    }


    @Bean
    public RabbitTemplate rabbitTemplate(){
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData = "+correlationData+",ack =" +ack+",cause = "+cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange = "+exchange+",route = "+routingKey+",replyCode = "+replyCode+",replyText = "+replyText+",message:"+message);
            }
        });
        return rabbitTemplate;
    }

    //  TODO：exchange -（direct） 队列 一个队列一个消费者

    @Bean
    public DirectExchange logUserExchange(){
        return new DirectExchange(env.getProperty("log.user.exchange.name"), true,false);
    }

    @Bean(name = "logUserQueue")
    public Queue logUserQueue(){
        return new Queue(env.getProperty("log.user.queue.name"), true);
    }

    @Bean
    public Binding logUserBinding(){
        return BindingBuilder.bind(logUserQueue()).to(logUserExchange()).with(env.getProperty("log.user.routing.key.name"));
    }


    /*//  TODO：exchange -（direct） 队列 一个队列一个消费者，共有多个队列

    @Bean
    public DirectExchange logUsersExchange(){
        return new DirectExchange(env.getProperty("log.users.exchange.name"), true,false);
    }


    @Bean(name = "logUserQueue1")
    public Queue logUserQueue1(){
        return new Queue(env.getProperty("log.users.queue.name1"), true);
    }

    @Bean(name = "logUserQueue2")
    public Queue logUserQueue2(){
        return new Queue(env.getProperty("log.users.queue.name2"), true);
    }


    @Bean
    public Binding logUserBindingA(){
        return BindingBuilder.bind(logUserQueue1()).to(logUsersExchange()).with(env.getProperty("log.users.routing.key.name1"));
    }

    @Bean
    public Binding logUserBindingB(){
        return BindingBuilder.bind(logUserQueue2()).to(logUsersExchange()).with(env.getProperty("log.users.routing.key.name2"));
    }
*/



}
