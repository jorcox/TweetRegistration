package es.unizar.tmdad.ucode.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import es.unizar.tmdad.ucode.domain.Attendee;


/**
 * Cada vez que se llama al método sendAttendee. En la clase ChooserPropagatorGatewayListener se redirige 
 * a la salida del chooser
 */
@Service
@Component
public interface ProcessorOutputService {

	public void sendAttendee(Attendee attendee);
	
}
