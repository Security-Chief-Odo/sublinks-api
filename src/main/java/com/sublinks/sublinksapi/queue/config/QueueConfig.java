package com.sublinks.sublinksapi.queue.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

  @Value("${sublinks.backend_queue.name}")
  private String backendQueueName;

  @Value("${sublinks.backend_topic.name}")
  private String backendTopicName;

  @Value("${sublinks.backend_routing_key}")
  private String backendRoutingKey;

  @Bean
  @ConditionalOnProperty(name =
      {"sublinks.backend_queue.name", "sublinks.backend_topic.name", "sublinks.federation.exchange",
          "sublinks.federation_queue.name"},
      matchIfMissing = false)
  public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {

    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  @ConditionalOnProperty(name =
      {"sublinks.backend_queue.name", "sublinks.backend_topic.name", "sublinks.federation.exchange",
          "sublinks.federation_queue.name"},
      matchIfMissing = false)
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory) {

    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }

  @Bean
  @ConditionalOnProperty(name =
      {"sublinks.backend_queue.name", "sublinks.backend_topic.name", "sublinks.federation.exchange",
          "sublinks.federation_queue.name"},
      matchIfMissing = false)
  public Jackson2JsonMessageConverter jsonMessageConverter() {

    return new Jackson2JsonMessageConverter();
  }

  @Bean
  @ConditionalOnProperty(name = {"sublinks.backend_queue.name", "sublinks.backend_topic.name",
      "sublinks.federation.exchange"}, matchIfMissing = false)
  public Queue federationQueue() {

    return new Queue(this.backendQueueName, true);
  }

  @Bean
  @ConditionalOnProperty(name = {"sublinks.backend_queue.name", "sublinks.backend_topic.name",
      "sublinks.federation.exchange"}, matchIfMissing = false)
  public TopicExchange federationTopicExchange() {

    return new TopicExchange(this.backendTopicName);
  }

  // @TODO: We will need to create multiple bindings if we have different routing keys for the backend.
  // This is assuming there is only one binding for our backend RabbitMQ exchange
  @Bean
  @ConditionalOnProperty(name = {"sublinks.backend_queue.name", "sublinks.backend_topic.name",
      "sublinks.federation.exchange"}, matchIfMissing = false)
  public Binding binding(Queue federationQueue, TopicExchange federationTopicExchange) {

    return BindingBuilder.bind(federationQueue).to(federationTopicExchange).with(backendRoutingKey);
  }
}
