package es.unizar.tmdad.ucode.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import es.unizar.tmdad.ucode.mail.MailingScheduler;

//@EnableWs
//@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
	
	/**
	 * Defines a special message dispatcher servlet for web services.
	 */
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	/**
	 * Defines the web service (location, namespace, etc)
	 */
	@Bean(name = "alerts")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema alertsSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("AlertsPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://imred.es/soap");
		wsdl11Definition.setSchema(alertsSchema);
		return wsdl11Definition;
	}

	/**
	 * Sets the location of the xsd file that defines the web service.
	 */
	@Bean
	public XsdSchema alertsSchema() {
		return new SimpleXsdSchema(new ClassPathResource("alerts.xsd"));
	}
	
	/**
	 * Sets the folder where the marshaller will find the web
	 * services.
	 */
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("es.imred.soap");
		return marshaller;
	}

	/**
	 * Registers a marshaller
	 */
	@Bean
	public MailingScheduler mailingScheduler(Jaxb2Marshaller marshaller) {
		MailingScheduler client = new MailingScheduler();
		client.setDefaultUri("http://localhost:8090/ws");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}
}
