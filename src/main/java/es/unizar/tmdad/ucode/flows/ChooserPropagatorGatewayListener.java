package es.unizar.tmdad.ucode.flows;

import org.springframework.integration.annotation.MessagingGateway;

import es.unizar.tmdad.ucode.service.ChooserPropagatorService;

/**
 * Se expone un bean con nombre "integrationStreamListener". Cada vez que un método
 * es invocado se traduce en un mensaje a "requestProcessorChannelTwitter"
 */
@MessagingGateway(name = "chooserPropagatorService", defaultRequestChannel = "requestChooserOutputChannel")
interface ChooserPropagatorGatewayListener extends ChooserPropagatorService{

}