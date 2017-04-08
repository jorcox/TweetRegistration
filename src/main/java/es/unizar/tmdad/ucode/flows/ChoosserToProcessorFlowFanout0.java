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
public class ChoosserToProcessorFlowFanout extends ChooserToProcessorFlow {

	final static String CHOOSER_TO_PROCESSOR_FANOUT_EXCHANGE = "chooser_to_processor_fanout";
	final static String CHOOSER_TO_PROCESSOR_FANOUT_A_QUEUE_NAME = "chooser_to_processor_fanout_queue";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ

	@Bean
	Queue aTwitterFanoutQueue() {
		return new Queue(CHOOSER_TO_PROCESSOR_FANOUT_A_QUEUE_NAME, false);
	}

	@Bean
	FanoutExchange twitterFanoutExchange() {
		return new FanoutExchange(CHOOSER_TO_PROCESSOR_FANOUT_EXCHANGE);
	}

	@Bean
	Binding twitterFanoutBinding() {
		return BindingBuilder.bind(aTwitterFanoutQueue()).to(
				twitterFanoutExchange());
	}

	// Flujo # ENVIAR
	//
	// MessageGateway Twitter -(requestChannelTwitter)-> MessageEndpoint
	// RabbitMQ
	//

	@Bean
	public DirectChannel requestProcessorChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpOutboundEndpoint amqpOutbound() {
		return Amqp.outboundAdapter(rabbitTemplate)
				.exchangeName(CHOOSER_TO_PROCESSOR_FANOUT_EXCHANGE).get();
	}

	@Bean
	public IntegrationFlow sendTweetToRabbitMQ() {
		/*
		 *  We take the tweets coming form the chooser send it 
		 *  to RabbitMQ queues in order to be get by processors
		 */		
		return IntegrationFlows.from(requestProcessorChannel())
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
/*
	@Override
	@Bean
	public DirectChannel requestChannelRabbitMQ() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpInbound() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aTwitterFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestChannelRabbitMQ()).get();
	}*/
}