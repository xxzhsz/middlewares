package com.hx.middleware.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author jxlgcmh
 * @date 2020-02-09 19:36
 * @description RabbitMQ配置文件
 */
@Configuration
public class RabbitMqConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);

    /**
     * rabbitmq连接工厂实例
     */
    @Autowired
    private CachingConnectionFactory connectionFactory;
    /**
     * 自动装配消息监听器所在的容器工厂配置类实例
     */
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;


    /**
     * 单一消费者实例配置
     *
     * @return
     */
    @Bean("singleListenerContainer")
    public SimpleRabbitListenerContainerFactory singleListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // 容器工厂所用实例
        factory.setConnectionFactory(connectionFactory);
        // 消息传输格式
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // 设置并发消费者实例初始化数量  设置为1
        factory.setConcurrentConsumers(1);
        // 设置最大并发消费者实例数量  设置为1
        factory.setMaxConcurrentConsumers(1);
        //设置拉取消息的数据量
        factory.setPrefetchCount(1);
        return factory;
    }

    /**
     * 多消费者实例配置
     *
     * @return
     */
    @Bean("multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // 容器工厂所用实例
        factoryConfigurer.configure(factory, connectionFactory);
        // 消息传输格式
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置消息确认模式  不需要确认消费
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        // 设置并发消费者实例初始化数量  设置为10
        factory.setConcurrentConsumers(10);
        // 设置最大并发消费者实例初始化数量  设置为15
        factory.setMaxConcurrentConsumers(15);
        //设置拉取消息的数据量
        factory.setPrefetchCount(10);
        return factory;
    }

    /**
     * 定义rabbitmq操作模板
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        //发送消息后进行确认
        connectionFactory.setPublisherConfirms(true);
        //发送消息后返回确认消息
        connectionFactory.setPublisherReturns(true);

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        /**
         * mandatory：交换器无法根据自身类型和路由键找到一个符合条件的队列时的处理方式
         * true：RabbitMQ会调用Basic.Return命令将消息返回给生产者
         * false：RabbitMQ会把消息直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        // 配置消息确发送成功的处理逻辑
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("发送消息成功：correlationData({}),ack({}),cause({})", correlationData, ack, cause));
        //配置消息确发送成功的处理逻辑
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息丢失：exchange({}),routingKey({}),replyText({}),replyCode({}),message({})", exchange, routingKey, replyText, replyCode, message);
        });
        return rabbitTemplate;
    }


    //定义读取配置文件的环境变量实例
    @Autowired
    private Environment env;

    //======================基本队列开始===================================================
    @Bean("basicQueue")
    public Queue basicQueue() {
        return new Queue(env.getProperty("mq.basic.info.queue.name"), true);
    }

    @Bean
    public DirectExchange basicExchange() {
        return new DirectExchange(env.getProperty("mq.basic.info.exchange.name"), true, false);
    }

    @Bean
    public Binding basicBinding() {
        return BindingBuilder.bind(basicQueue()).to(basicExchange()).with(env.getProperty("mq.basic.info.routing.key.name"));
    }
//===========================基本队列结束==============================================


    //======================对象传输开始===================================================
    @Bean("objectQueue")
    public Queue objectQueue() {
        return new Queue(env.getProperty("mq.object.info.queue.name"), true);
    }

    @Bean
    public DirectExchange objectExchange() {
        return new DirectExchange(env.getProperty("mq.object.info.exchange.name"), true, false);
    }

    @Bean
    public Binding objectBinding() {
        return BindingBuilder.bind(objectQueue()).to(objectExchange()).with(env.getProperty("mq.object.info.routing.key.name"));
    }
//===========================对象传输开始==============================================


    //===========================fanout配置==============================================
    //创建队列1
    @Bean(name = "fanoutQueueOne")
    public Queue fanoutQueueOne() {
        return new Queue(env.getProperty("mq.fanout.queue.name.one"), true);
    }

    //创建队列2
    @Bean(name = "fanoutQueueTwo")
    public Queue fanoutQueueTwo() {
        return new Queue(env.getProperty("mq.fanout.queue.name.two"), true);
    }

    //创建交换机-fanoutExchange
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(env.getProperty("mq.fanout.exchange.name"), true, false);
    }

    //创建绑定1
    @Bean
    public Binding fanoutBindingOne() {
        return BindingBuilder.bind(fanoutQueueOne()).to(fanoutExchange());
    }

    //创建绑定2
    @Bean
    public Binding fanoutBindingTwo() {
        return BindingBuilder.bind(fanoutQueueTwo()).to(fanoutExchange());
    }
//===========================fanout配置==============================================


    //===========================direct配置==============================================
    @Bean("directQueueOne")
    public Queue directQueueOne() {
        return new Queue(env.getProperty("mq.direct.queue.name.one"));
    }

    @Bean("directQueueTwo")
    public Queue directQueueTwo() {
        return new Queue(env.getProperty("mq.direct.queue.name.two"));
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(env.getProperty("mq.direct.exchange.name"), true, false);
    }

    @Bean
    public Binding directBindingOne() {
        return BindingBuilder.bind(directQueueOne()).to(directExchange()).with(env.getProperty("mq.direct.routing.key.name.one"));
    }

    @Bean
    public Binding directBindingTwo() {
        return BindingBuilder.bind(directQueueTwo()).to(directExchange()).with(env.getProperty("mq.direct.routing.key.name.two"));
    }
//===========================direct配置==============================================


    // ===========================topic配置==============================================
    @Bean("topicQueueOne")
    public Queue topicQueueOne() {
        return new Queue(env.getProperty("mq.topic.queue.name.one"));
    }

    @Bean("topicQueueTwo")
    public Queue topicQueueTwo() {
        return new Queue(env.getProperty("mq.topic.queue.name.two"));
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(env.getProperty("mq.topic.exchange.name"), true, false);
    }

    @Bean
    public Binding topicBindingOne() {
        return BindingBuilder.bind(topicQueueOne()).to(topicExchange()).with(env.getProperty("mq.topic.routing.key.name.one"));
    }

    @Bean
    public Binding topicBindingTwo() {
        return BindingBuilder.bind(topicQueueTwo()).to(topicExchange()).with(env.getProperty("mq.topic.routing.key.name.two"));
    }
// ===========================topic配置==============================================


}
