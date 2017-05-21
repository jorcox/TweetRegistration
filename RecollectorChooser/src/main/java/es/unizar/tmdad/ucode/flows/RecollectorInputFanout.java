package es.unizar.tmdad.ucode.flows;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.social.twitter.api.Tweet;


@Configuration
@Profile("fanout")
public class RecollectorInputFanout extends RecollectorInputFlow {

	final static String TWITTER_FANOUT_EXCHANGE = "twitter_fanout";
	final static String TWITTER_FANOUT_A_QUEUE_NAME_OUT = "twitter_fanout_queue_out";
	final static String CHOOSER_FANOUT_A_QUEUE_NAME_IN = "chooser_fanout_queue_in";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ
	
	@Bean
	Queue outTwitterFanoutQueue() {
		return new Queue(TWITTER_FANOUT_A_QUEUE_NAME_OUT, false);
	}

	@Bean
	Queue inChooserFanoutQueue() {
		return new Queue(CHOOSER_FANOUT_A_QUEUE_NAME_IN, false);
	}

	@Bean
	FanoutExchange twitterFanoutExchange() {
		return new FanoutExchange(TWITTER_FANOUT_EXCHANGE);
	}

	@Bean
	Binding outTwitterFanoutBinding() {
		return BindingBuilder.bind(outTwitterFanoutQueue()).to(
				twitterFanoutExchange());
	}
	
	@Bean
	Binding inChooserFanoutBinding() {
		return BindingBuilder.bind(inChooserFanoutQueue()).to(
				twitterFanoutExchange());
	}

	// Flujo #1
	//
	// MessageGateway Twitter -(requestChannelTwitter)-> MessageEndpoint
	// RabbitMQ
	//
	// RECOLECTOR to RabbitMQ
	//

	@Override
	@Bean
	/* 
	 * Input channel for twitter API
	 */
	public DirectChannel requestChooserChannelTwitter() {
		return MessageChannels.direct().get();
	}

	/*@Bean
	public AmqpOutboundEndpoint amqpOutbound() {
		return Amqp.outboundAdapter(rabbitTemplate)
				.exchangeName(TWITTER_FANOUT_EXCHANGE).get();
	}

	@Bean
	public IntegrationFlow sendTweetToRabbitMQ() {*/
		/*
		 *  We take the tweets coming form the Streaming API of Twiiter and send it 
		 *  to RabbitMQ queues
		 */		
		/*return IntegrationFlows.from(requestChooserChannelTwitter())
				//.transform(highlight())		// Debug Purposes
				.handle(amqpOutbound()).get();
	}*/
	
	/*private GenericTransformer<Tweet, Tweet> highlight() {
		return t -> {			
			//String tag = t.getFirstTarget();
			String text = t.getUnmodifiedText();
			System.out.println("PRE --> " + text);
			//t.getTweet().setUnmodifiedText(
			//		text.replaceAll(tag, "<b>" + tag + "</b>"));
			return t;
		};
	}*/
	

	// Flujo #2
	//
	// MessageEndpoint RabbitMQ -(requestChannelRabbitMQ)-> tareas ...
	//
	// CHOOSER from RabbitMQ
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

	/*@Bean
	public AmqpInboundChannelAdapter amqpInboundChooser1() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(inChooserFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChooserChannelTwitter()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundChooser2() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(inChooserFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser2()).get();
	}
	
	@Bean
	public AmqpInboundChannelAdapter amqpInboundChooser3() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(inChooserFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQChooser3()).get();
	}*/

}