package es.unizar.tmdad.ucode.flows;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.integration.dsl.channel.MessageChannels;

@Configuration
@Profile("topic")
public class RecollectorInputTopic extends RecollectorInputFlow {

	final static String TWITTER_TOPIC_EXCHANGE = "twitter_topic";
	final static String TWITTER_TOPIC_A_QUEUE_NAME = "twitter_topic_queue";
	final static String TWITTER_TOPIC_PATTERN = "twitter_topic.*";
	final static String TWITTER_TOPIC_A_ROUTING_KEY_VALUE = "'twitter_topic.in'";
	final static String DYNAMIC_ROUTING_KEY_VALUE = "{T(java.lang.Math).random() < 0.5 ? "
			+ TWITTER_TOPIC_A_ROUTING_KEY_VALUE + " : 'dummy'}";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ

	@Bean
	Queue aTwitterTopicQueue() {
		return new Queue(TWITTER_TOPIC_A_QUEUE_NAME, false);
	}

	@Bean
	TopicExchange twitterTopicExchange() {
		return new TopicExchange(TWITTER_TOPIC_EXCHANGE);
	}

	@Bean
	Binding twitterTopicBinding() {
		return BindingBuilder.bind(aTwitterTopicQueue())
				.to(twitterTopicExchange()).with(TWITTER_TOPIC_PATTERN);
	}

	// Flujo #1
	//
	// MessageGateway Twitter -(requestChannelTwitter)-> MessageEndpoint
	// RabbitMQ
	//

	@Override
	@Bean
	public DirectChannel requestChooserChannelTwitter() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpOutboundEndpoint amqpOutbound() {
		return Amqp.outboundAdapter(rabbitTemplate)
				.exchangeName(TWITTER_TOPIC_EXCHANGE)
				.routingKeyExpression("headers['routingKey']").get();
	}

	@Bean
	public IntegrationFlow sendTweetToRabbitMQ() {
		return IntegrationFlows
				.from(requestChooserChannelTwitter())
				.enrichHeaders(
						s -> s.headerExpressions(h -> h.put("routingKey",
								TWITTER_TOPIC_A_ROUTING_KEY_VALUE)))
				.handle(amqpOutbound()).get();
	}

	// Flujo #2
	//
	// MessageEndpoint RabbitMQ -(requestChannelRabbitMQ)-> tareas ...
	//

	@Override
	@Bean
	public DirectChannel requestChannelRabbitMQChooser1() {
		return MessageChannels.direct().get();
	}
	
	@Override
	@Bean
	public DirectChannel requestChannelRabbitMQChooser2() {
		return MessageChannels.direct().get();
	}
	
	@Override
	@Bean
	public DirectChannel requestChannelRabbitMQChooser3() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpInboundUpdater() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterTopicQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser1()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundSaver() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterTopicQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser2()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundProcessor() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterTopicQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser3()).get();
	}
}