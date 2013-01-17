package eu.tsp.hess;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.freemarker.ContextTemplateLoader;
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
		
		Configuration cfg = new Configuration();

		ContextTemplateLoader loader = new ContextTemplateLoader(getContext(),"war:///view"); 

		cfg.setTemplateLoader(loader);

		TemplateRepresentation rep = null;

		Mail mail = new Mail(); 
		mail.setStatus("received"); 
		mail.setSubject("Message to self");
		mail.setContent("Doh!");

		  // Prepare the data model
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("mail", mail);

		

		rep = new TemplateRepresentation("Mail.ftl", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;

	}

}
