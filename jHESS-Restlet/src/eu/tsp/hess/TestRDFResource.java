package eu.tsp.hess;

import org.restlet.data.Reference;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Literal;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class TestRDFResource extends ServerResource {
	
	@Get("rdf") 
	public Representation toRdf() throws ResourceException { 
		String FOAF_BASE = "http://xmlns.com/foaf/0.1/";
		Reference firstName = new Reference(FOAF_BASE + "firstName");
		Reference lastName = new Reference(FOAF_BASE + "lastName");
		Reference mbox = new Reference(FOAF_BASE + "mbox");
		Reference knows =  new Reference(FOAF_BASE + "knows");
		Reference homerRef = new Reference(
		"http://www.rmep.org/accounts/chunkylover53/");
		Reference margeRef = new Reference(
		"http://www.rmep.org/accounts/bretzels34/");
		Reference bartRef = new Reference(
		"http://www.rmep.org/accounts/jojo10/");
		Reference lisaRef = new Reference(
		"http://www.rmep.org/accounts/lisa1984/");
		Graph example = new Graph();
		example.add(homerRef, firstName, new Literal("Homer"));
		example.add(homerRef, lastName, new Literal("Simpson"));
		example.add(homerRef, mbox, new Literal(
		"mailto:homer@simpson.org"));
		example.add(homerRef, knows, margeRef);
		example.add(homerRef, knows, bartRef);
		example.add(homerRef, knows, lisaRef);
		return example.getRdfXmlRepresentation();
		
	}

}
