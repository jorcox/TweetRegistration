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
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.integration.dsl.channel.MessageChannels;

@Configuration
@Profile("fanout")
public class ProcessorFlowFanout extends ProcessorFlow {

	final static String CHOOSER_OUTPUT_FANOUT_EXCHANGE = "chooser_output_fanout";
	final static String CHOOSER_OUTPUT_FANOUT_A_QUEUE_NAME = "chooser_output_fanout_queue";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ

	/*@Bean
	Queue aChooserFanoutQueue() {
		return new Queue(CHOOSER_OUTPUT_FANOUT_A_QUEUE_NAME, false);
	}

	@Bean
	FanoutExchange chooserFanoutExchange() {
		return new FanoutExchange(CHOOSER_OUTPUT_FANOUT_EXCHANGE);
	}

	@Bean
	Binding twitterFanoutBinding() {
		return BindingBuilder.bind(aChooserFanoutQueue()).to(
				chooserFanoutExchange());
	}*/

	// Flujo # ENVIAR
	//
	// MessageGateway Twitter -(requestChannelTwitter)-> MessageEndpoint
	// RabbitMQ
	//

	/*@Bean
	public DirectChannel requestChooserOutputChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpOutboundEndpoint amqpChooserOutbound() {
		return Amqp.outboundAdapter(rabbitTemplate)
				.exchangeName(CHOOSER_OUTPUT_FANOUT_EXCHANGE).get();
	}*/

	/*@Bean
	public IntegrationFlow sendTargetedTweetToRabbitMQ() {
		/*
		 *  We take the tweets coming form the chooser send it 
		 *  to RabbitMQ queues in order to be get by processors
		 */		
		/*return IntegrationFlows.from(requestChooserOutputChannel())
				.transform(highlight())		// Debug Purposes
				.handle(amqpChooserOutbound()).get();
	}
	
	private GenericTransformer<TargetedTweet, TargetedTweet> highlight() {
		return t -> {			
			//String tag = t.getFirstTarget();
			String text = t.getTweet().getUnmodifiedText();
			System.out.println("CHOOSER --> " + text);
			//t.getTweet().setUnmodifiedText(
			//		text.replaceAll(tag, "<b>" + tag + "</b>"));
			return t;
		};
	}*/
	

	// Flujo #2
	//
	// MessageEndpoint RabbitMQ -(requestChannelRabbitMQ)-> tareas ...
	//

	/*@Override
	@Bean
	public DirectChannel requestSaverChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpSaverInbound() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aChooserFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestSaverChannel()).get();
	}*/
}