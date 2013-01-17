package eu.tsp.hess;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import freemarker.template.Configuration;

public class TestHTMLResource extends ServerResource {
	
	@Get 
	public Representation toHTML() throws ResourceException { 
		Mail mail = new Mail();
		mail.setStatus("received");
		mail.setSubject("Message to self");
		mail.setContent("Doh!");
		
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("mail", mail);
		Configuration fmConfig = new Configuration();
		
		
		Representation mailFtl = new ClientResource("/view/Mail.ftl").get();
		
		return new TemplateRepresentation(mailFtl, dataModel,
		MediaType.TEXT_HTML);
		
	}

}
