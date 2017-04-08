package es.unizar.tmdad.ucode.flows;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
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
import org.springframework.social.twitter.api.Tweet;

@Configuration
@Profile("fanout")
public class TwitterToRecollectorToChooserFanout extends TwitterToRecollectorToChooserFlow {

	final static String TWITTER_FANOUT_EXCHANGE = "twitter_fanout";
	final static String TWITTER_FANOUT_A_QUEUE_NAME = "twitter_fanout_queue";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ

	@Bean
	Queue aTwitterFanoutQueue() {
		return new Queue(TWITTER_FANOUT_A_QUEUE_NAME, false);
	}

	@Bean
	FanoutExchange twitterFanoutExchange() {
		return new FanoutExchange(TWITTER_FANOUT_EXCHANGE);
	}

	@Bean
	Binding twitterFanoutBinding() {
		return BindingBuilder.bind(aTwitterFanoutQueue()).to(
				twitterFanoutExchange());
	}

	// Flujo #1
	//
	// MessageGateway Twitter -(requestChannelTwitter)-> MessageEndpoint
	// RabbitMQ
	//
	// RECOLECTOR to RabbitMQ
	//

	@Bean
	public DirectChannel requestChooserChannelTwitter() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpOutboundEndpoint amqpOutbound() {
		return Amqp.outboundAdapter(rabbitTemplate)
				.exchangeName(TWITTER_FANOUT_EXCHANGE).get();
	}

	@Bean
	public IntegrationFlow sendTweetToRabbitMQ() {
		/*
		 *  We take the tweets coming form the Streaming API of Twiiter and send it 
		 *  to RabbitMQ queues
		 */		
		return IntegrationFlows.from(requestChooserChannelTwitter())
				//.transform(highlight())		// Debug Purposes
				.handle(amqpOutbound()).get();
	}
	
	private GenericTransformer<Tweet, Tweet> highlight() {
		return t -> {			
			//String tag = t.getFirstTarget();
			String text = t.getUnmodifiedText();
			System.out.println("PRE --> " + text);
			//t.getTweet().setUnmodifiedText(
			//		text.replaceAll(tag, "<b>" + tag + "</b>"));
			return t;
		};
	}
	

	// Flujo #2
	//
	// MessageEndpoint RabbitMQ -(requestChannelRabbitMQ)-> tareas ...
	//
	// CHOOSER from RabbitMQ
	//

	@Override
	@Bean
	public DirectChannel requestChannelRabbitMQUpdater() {
		return MessageChannels.direct().get();
	}
	
	@Override
	@Bean
	public DirectChannel requestChannelRabbitMQSaver() {
		return MessageChannels.direct().get();
	}
	
	@Override
	@Bean
	public DirectChannel requestChannelRabbitMQProccessor() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpInboundUpdater() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQUpdater()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundSaver() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQSaver()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundProcessor() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQProccessor()).get();
	}

}