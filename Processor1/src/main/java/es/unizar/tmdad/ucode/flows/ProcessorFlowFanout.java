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

import es.unizar.tmdad.ucode.domain.Attendee;

@Configuration
@Profile("fanout")
public class ProcessorFlowFanout extends ProcessorFlow {

	final static String CHOOSER_OUTPUT_FANOUT_EXCHANGE = "chooser_output_fanout";
	final static String PROCESSOR_OUTPUT_FANOUT_EXCHANGE = "processor_output_fanout";
	
	final static String PROCESSOR_INPUT_FANOUT_A_QUEUE_NAME = "processor_input_fanout_queue";
	final static String PROCESSOR_OUTPUT_FANOUT_A_QUEUE_NAME = "updater_input_fanout_attendee_queue";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ
	
	@Bean
	Queue aProcessorOutputFanoutQueue() {
		return new Queue(PROCESSOR_OUTPUT_FANOUT_A_QUEUE_NAME, false);
	}

	@Bean
	FanoutExchange processorOutputFanoutExchange() {
		return new FanoutExchange(PROCESSOR_OUTPUT_FANOUT_EXCHANGE);
	}

	@Bean
	Binding processorOutputFanoutBinding() {
		return BindingBuilder.bind(aProcessorOutputFanoutQueue()).to(
				processorOutputFanoutExchange());
	}

	@Bean
	Queue aProcessorFanoutQueue() {
		return new Queue(PROCESSOR_INPUT_FANOUT_A_QUEUE_NAME, false);
	}

	@Bean
	FanoutExchange processorFanoutExchange() {
		return new FanoutExchange(CHOOSER_OUTPUT_FANOUT_EXCHANGE);
	}

	@Bean
	Binding processorFanoutBinding() {
		return BindingBuilder.bind(aProcessorFanoutQueue()).to(
				processorFanoutExchange());
	}
	
	// Flujo #1 
	//
	// Output RabbitMQ
	
	@Bean
	public DirectChannel requestProcessorOutputChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpOutboundEndpoint amqpProcessorOutbound() {
		return Amqp.outboundAdapter(rabbitTemplate)
				.exchangeName(PROCESSOR_OUTPUT_FANOUT_EXCHANGE).get();
	}

	@Bean
	public IntegrationFlow sendAttendeeToRabbitMQ() {
		/*
		 *  We take the tweets coming form the chooser send it 
		 *  to RabbitMQ queues in order to be get by processors
		 */		
		return IntegrationFlows.from(requestProcessorOutputChannel())
				//.transform(Transformers.toJson())
				.transform(highlight())		// Debug Purposes
				.handle(Amqp.outboundAdapter(rabbitTemplate)
						.exchangeName(PROCESSOR_OUTPUT_FANOUT_EXCHANGE)).get();
	}
	
	private GenericTransformer<Attendee, Attendee > highlight() {
		return at -> {			
			//String tag = t.getFirstTarget();
			//String text = t.getTweet().getUnmodifiedText();
			System.out.println("PROCESSOR OUTPUT");
			//t.getTweet().setUnmodifiedText(
			//		text.replaceAll(tag, "<b>" + tag + "</b>"));
			return at;
		};
	}

	// Flujo #2
	//
	// MessageEndpoint RabbitMQ -(requestChannelRabbitMQ)-> tareas ...
	//

	@Override
	@Bean
	public DirectChannel requestProcessorChannel() {
		return MessageChannels.direct().get();
	}

	@Bean	
	public AmqpInboundChannelAdapter amqpProcessorInbound() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aProcessorFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestProcessorChannel()).get();
	}
}