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
public class SaverFlowFanout extends SaverFlow {

	final static String CHOOSER_OUTPUT_FANOUT_EXCHANGE = "chooser_output_fanout";
	final static String SAVER_INPUT_FANOUT_A_QUEUE_NAME = "saver_input_fanout_queue";

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Configuración RabbitMQ

	@Bean
	Queue aSaverFanoutQueue() {
		return new Queue(SAVER_INPUT_FANOUT_A_QUEUE_NAME, false);
	}

	@Bean
	FanoutExchange saverFanoutExchange() {
		return new FanoutExchange(CHOOSER_OUTPUT_FANOUT_EXCHANGE);
	}

	@Bean
	Binding saverFanoutBinding() {
		return BindingBuilder.bind(aSaverFanoutQueue()).to(
				saverFanoutExchange());
	}

	// Flujo #2
	//
	// MessageEndpoint RabbitMQ -(requestChannelRabbitMQ)-> tareas ...
	//

	@Override
	@Bean
	public DirectChannel requestSaverChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public AmqpInboundChannelAdapter amqpSaverInbound() {
		SimpleMessageListenerContainer smlc = new SimpleMessageListenerContainer(
				rabbitTemplate.getConnectionFactory());
		smlc.setQueues(aSaverFanoutQueue());
		return Amqp.inboundAdapter(smlc)
				.outputChannel(requestSaverChannel()).get();
	}
}