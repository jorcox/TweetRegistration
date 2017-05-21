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
@Profile("dashboard")
public class UpdaterFlowFanout extends UpdaterFlow {

	final static String CHOOSER_OUTPUT_FANOUT_EXCHANGE = "chooser_output_fanout";
	final static String UPDATER_INPUT_FANOUT_A_QUEUE_NAME = "updater_input_fanout_queue";
	
	final static String PROCESSOR_OUTPUT_FANOUT_EXCHANGE = "processor_output_fanout";
	final static String UPDATER_INPUT_FANOUT_ATTENDE_QUEUE = "updater_input_fanout_attendee_queue";
	
	final static String PROCESSOR2_OUTPUT_FANOUT_EXCHANGE = "processor2_output_fanout";
	final static String UPDATER_INPUT_FANOUT_ATTENDE_QUEUE_2 = "updater_input_fanout_attendee_queue_2";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ

	@Bean
	Queue aUpdaterFanoutQueue() {
		return new Queue(UPDATER_INPUT_FANOUT_A_QUEUE_NAME, false);
	}

	@Bean
	FanoutExchange updaterFanoutExchange() {
		return new FanoutExchange(CHOOSER_OUTPUT_FANOUT_EXCHANGE);
	}

	@Bean
	Binding updaterFanoutBinding() {
		return BindingBuilder.bind(aUpdaterFanoutQueue()).to(
				updaterFanoutExchange());
	}
	
	@Bean
	Queue aUpdaterFanoutAttendeeQueue() {
		return new Queue(UPDATER_INPUT_FANOUT_ATTENDE_QUEUE, false);
	}

	@Bean
	FanoutExchange updaterFanoutAttendeeExchange() {
		return new FanoutExchange(PROCESSOR_OUTPUT_FANOUT_EXCHANGE);
	}

	@Bean
	Binding updaterFanoutAttendeBinding() {
		return BindingBuilder.bind(aUpdaterFanoutAttendeeQueue()).to(
				updaterFanoutAttendeeExchange());
	}
	
	@Bean
	Queue aUpdaterFanoutAttendeeQueue2() {
		return new Queue(UPDATER_INPUT_FANOUT_ATTENDE_QUEUE_2, false);
	}

	@Bean
	FanoutExchange updaterFanoutAttendeeExchange2() {
		return new FanoutExchange(PROCESSOR2_OUTPUT_FANOUT_EXCHANGE);
	}

	@Bean
	Binding updaterFanoutAttendeBinding2() {
		return BindingBuilder.bind(aUpdaterFanoutAttendeeQueue2()).to(
				updaterFanoutAttendeeExchange2());
	}

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
	

	// Flujo #1
	//
	// Chooser -> Updater
	//

	@Override
	@Bean
	public DirectChannel requestUpdateChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpUpdaterInbound() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aUpdaterFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestUpdateChannel()).get();
	}
	
	// Flujo #2
	//
	// Processor1 -> Updater
	//

	@Override
	@Bean
	public DirectChannel requestUpdateAttendeesChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpUpdaterAttendeesInbound() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aUpdaterFanoutAttendeeQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestUpdateAttendeesChannel()).get();
	}
	
	// Flujo #3
	//
	// Processor2 -> Updater
	//

	@Override
	@Bean
	public DirectChannel requestUpdateAttendeesChannel2() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpUpdaterAttendeesInbound2() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aUpdaterFanoutAttendeeQueue2());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestUpdateAttendeesChannel2()).get();
	}
}