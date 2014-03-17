package com.orange.ucp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class ApproachStatusResource extends ServerResource {

	@Get
	public String processResourceStatus() throws JSONException {

		String did = (String) getRequestAttributes().get("did");
		String status = (String) getRequestAttributes().get("status");

		if (did.equalsIgnoreCase("4")) {
			// TODO: change 1
			getResponse().redirectSeeOther("/jUCP/approach/4");
			return "/jUCP/approach/," + did + "," + status;

		} else if (did.equalsIgnoreCase("5")) {
			// TODO: change 2
			getResponse().redirectSeeOther("/jUCP/approach/5");
			return "/jUCP/approach/," + did + "," + status;

		} else if (did.equalsIgnoreCase("6")) {
			// TODO: change 3
			getResponse().redirectSeeOther("/jUCP/approach/6");
			return "/jUCP/approach/," + did + "," + status;

		} else {
			return "";

		}

	}

}
