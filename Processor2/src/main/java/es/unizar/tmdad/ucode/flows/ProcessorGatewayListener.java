package es.unizar.tmdad.ucode.flows;

import org.springframework.integration.annotation.MessagingGateway;

import es.unizar.tmdad.ucode.service.ProcessorOutputService;

/**
 * Se expone un bean con nombre "processorOutputService". Cada vez que un método
 * es invocado se traduce en un mensaje a "requestProcessorChannelTwitter"
 */
@MessagingGateway(name = "processorOutputService", defaultRequestChannel = "requestProcessorOutputChannel")
interface ProcessorGatewayListener extends ProcessorOutputService{

}