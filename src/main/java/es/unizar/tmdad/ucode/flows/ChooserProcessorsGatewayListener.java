package es.unizar.tmdad.ucode.flows;

import org.springframework.integration.annotation.MessagingGateway;

import es.unizar.tmdad.ucode.service.ChooserProccessorService;

/**
 * Se expone un bean con nombre "integrationStreamListener". Cada vez que un método
 * es invocado se traduce en un mensaje a "requestProcessorChannelTwitter"
 */
@MessagingGateway(name = "chooserProccessorService", defaultRequestChannel = "requestChooserOutputChannel")
interface ChooserProcessorsGatewayListener extends ChooserProccessorService{

}