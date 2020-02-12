package com.hx.middleware.server.config;

import com.hx.middleware.server.rabbitmq.consumer.KnowledgeConsumer;
import com.hx.middleware.server.rabbitmq.consumer.KnowledgeManualConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

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
     * 单一消费者，消费消费后的确认模式为auto
     *
     * @return
     */
    @Bean("listenerContainerAutoFactory")
    public SimpleRabbitListenerContainerFactory listenerContainerAutoFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        //设置消息消费的确认模式 ===设置为自动确认
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
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


    // ===========================auto自动确认机制消息队列配置==============================================、
    @Bean("autoQueue")
    public Queue autoQueue() {
        return new Queue(env.getProperty("mq.auto.knowledge.queue.name"));
    }

    @Bean
    public DirectExchange autoExchange() {
        return new DirectExchange(env.getProperty("mq.auto.knowledge.exchange.name"), true, false);
    }

    @Bean
    public Binding autoBinding() {
        return BindingBuilder.bind(autoQueue()).to(autoExchange()).with(env.getProperty("mq.auto.knowledge.routing.key.name"));
    }
// ===========================auto自动确认机制消息队列配置==============================================


    // ===========================manual手动确认配置==============================================
    @Bean("manualQueue")
    public Queue manualQueue() {
        return new Queue(env.getProperty("mq.manual.knowledge.queue.name"), true);
    }

    @Bean
    public DirectExchange manualExchange() {
        return new DirectExchange(env.getProperty("mq.manual.knowledge.exchange.name"), true, false);
    }

    @Bean
    public Binding manualBinding() {
        return BindingBuilder.bind(manualQueue()).to(manualExchange()).with(env.getProperty("mq.manual.knowledge.routing.key.name"));
    }

    /**
     * 注入手动确认消费者实例
     */
    @Autowired
    private KnowledgeManualConsumer knowledgeManualConsumer;

    @Bean("simpleContainerManual")
    public SimpleMessageListenerContainer simpleContainer(@Qualifier("manualQueue") Queue manualQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageConverter(new Jackson2JsonMessageConverter());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setPrefetchCount(1);
        // 设置确认机制
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueues(manualQueue);
        container.setMessageListener(knowledgeManualConsumer);
        // 返回工厂实例
        return container;
    }


    // ===========================用户日志记录==============================================
    @Bean("loginQueue")
    public Queue loginQueue() {
        return new Queue(env.getProperty("mq.login.queue.name"));
    }

    @Bean
    public DirectExchange loginExchange() {
        return new DirectExchange(env.getProperty("mq.login.exchange.name"));
    }

    @Bean
    public Binding loginBinding() {
        return BindingBuilder.bind(loginQueue()).to(loginExchange()).with(env.getProperty("mq.login.routing.key.name"));
    }


    // ===========================死信队列==============================================

    /**
     * 创建死信队列
     *
     * @return
     */
    @Bean
    public Queue basicDeadQueue() {
        Map<String, Object> map = new HashMap<>();
        // 死信交换机
        map.put("x-dead-letter-exchange", env.getProperty("mq.dead.exchange.name"));
        // 死信路由
        map.put("x-dead-letter-routing-key", env.getProperty("mq.dead.routing.key.name"));
        // 设置ttl ,此处设置为10秒
        map.put("x-message-ttl", 10000);
        return new Queue(env.getProperty("mq.dead.queue.name"), true, false, false, map);
    }

    /**
     * 创建交换机，面向生产者
     *
     * @return
     */
    @Bean
    public TopicExchange basicProducerExchange() {
        return new TopicExchange(env.getProperty("mq.producer.basic.exchange.name"), true, false);
    }

    /**
     * 创建死信队列绑定
     *
     * @return
     */
    @Bean
    public Binding basicProducerBinding() {
        return BindingBuilder.bind(basicDeadQueue()).to(basicProducerExchange()).with(env.getProperty("mq.producer.basic.routing.key.name"));
    }

    /**
     * 真正的消费者队列
     *
     * @return
     */
    @Bean
    public Queue realConsumerQueue() {
        return new Queue(env.getProperty("mq.consumer.queue.name"), true);
    }

    @Bean
    public TopicExchange basicDeadExchange() {
        return new TopicExchange(env.getProperty("mq.dead.exchange.name"), true, false);
    }

    @Bean
    public Binding basicDeadBinding() {
        return BindingBuilder.bind(realConsumerQueue()).to(basicDeadExchange()).with(env.getProperty("mq.dead.routing.key.name"));
    }

    // ===========================死信队列==============================================


// ===========================用户订单死信队列==============================================

    @Bean
    public Queue orderDeadQueue() {
        Map<String, Object> map = new HashMap<>();
        // 死信交换机
        map.put("x-dead-letter-exchange", env.getProperty("mq.order.dead.exchange.name"));
        // 死信路由
        map.put("x-dead-letter-routing-key", env.getProperty("mq.order.dead.routing.key.name"));
        // 设置ttl ,此处设置为10秒
        map.put("x-message-ttl", 10000);
        return new Queue(env.getProperty("mq.order.dead.queue.name"), true, false, false, map);
    }

    @Bean
    public TopicExchange orderProducerExchange() {
        return new TopicExchange(env.getProperty("mq.order.producer.basic.exchange.name"), true, false);
    }

    @Bean
    public Binding orderProducerBinding() {
        return BindingBuilder.bind(orderDeadQueue()).to(orderProducerExchange()).with(env.getProperty("mq.order.producer.basic.routing.key.name"));
    }

    @Bean
    public Queue realOrderConsumerQueue() {
        return new Queue(env.getProperty("mq.order.consumer.queue.name"), true);
    }

    @Bean
    public TopicExchange basicOrderDeadExchange() {
        return new TopicExchange(env.getProperty("mq.order.dead.exchange.name"), true, false);
    }

    @Bean
    public Binding basicOrderDeadBinding() {
        return BindingBuilder.bind(realOrderConsumerQueue()).to(basicOrderDeadExchange()).with(env.getProperty("mq.order.dead.routing.key.name"));
    }

// ===========================用户订单死信队列==============================================
// ===========================用户订单死信队列==============================================


}
