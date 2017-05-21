package es.unizar.tmdad.ucode.flows;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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
import org.springframework.integration.transformer.GenericTransformer;

import es.unizar.tmdad.ucode.domain.TargetedTweet;

@Configuration
@Profile("direct")
public class RecollectorInputDirect extends RecollectorInputFlow {

	final static String TWITTER_DIRECT_EXCHANGE = "twitter_direct";
	final static String TWITTER_DIRECT_A_QUEUE_NAME = "twitter_direct_queue";
	final static String TWITTER_DIRECT_A_ROUTING_KEY = TWITTER_DIRECT_A_QUEUE_NAME;

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración obligatoria RabbitMQ

	@Bean
	Queue aTwitterDirectQueue() {
		return new Queue(TWITTER_DIRECT_A_QUEUE_NAME, false);
	}

	@Bean
	DirectExchange twitterDirectExchange() {
		return new DirectExchange(TWITTER_DIRECT_EXCHANGE);
	}

	@Bean
	Binding twitterDirectBinding() {
		return BindingBuilder.bind(aTwitterDirectQueue())
				.to(twitterDirectExchange()).with(TWITTER_DIRECT_A_ROUTING_KEY);
	}

	/* 
	 * Flujo #1
	 * 
	 * MessageGateway Twitter -(requestChannelTwitter)-> MessageEndpoint
	 * RabbitMQ
	 * 
	 * RECOLLECTOR OUTPUT 
	 */
	
	@Override
	@Bean
	public DirectChannel requestChooserChannelTwitter() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpOutboundEndpoint amqpOutbound() {
		return Amqp.outboundAdapter(rabbitTemplate)
				.exchangeName(TWITTER_DIRECT_EXCHANGE)
				.routingKey(TWITTER_DIRECT_A_ROUTING_KEY).get();
	}
	
	private GenericTransformer<TargetedTweet, TargetedTweet> highlight() {
		return t -> {			
			//String tag = t.getFirstTarget();
			String text = t.getTweet().getUnmodifiedText();
			System.out.println("PRE --> " + text);
			//t.getTweet().setUnmodifiedText(
			//		text.replaceAll(tag, "<b>" + tag + "</b>"));
			return t;
		};
	}

	@Bean
	public IntegrationFlow sendTweetToRabbitMQ() {
		/*return IntegrationFlows.from(requestChannelTwitter())
				.transform(highlight())
				.handle("streamSendingService", "sendTweet").get();*/
		return IntegrationFlows.from(requestChooserChannelTwitter())
				//.transform(highlight())
				.handle(amqpOutbound()).get();
	}

	// Flujo #2
	//
	// MessageEndpoint RabbitMQ -(requestChannelRabbitMQ)-> tareas ...
	//

	/*@Override
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
	}*/
/*
	@Bean
	public AmqpInboundChannelAdapter amqpInboundUpdater() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterDirectQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser1()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundSaver() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterDirectQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser2()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundProcessor() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterDirectQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser3()).get();
	}*/

}
