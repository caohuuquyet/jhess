package eu.tsp.hess;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class TestJSONResource extends ServerResource {

	@Get("json")
	public Representation toJSON() throws JSONException {
		JSONObject mailElt = new JSONObject();
		mailElt.put("status", "received"); 
		mailElt.put("subject", "Message to self");
		mailElt.put("content", "Doh!");
		
		return new JsonRepresentation(mailElt);
	}

}
