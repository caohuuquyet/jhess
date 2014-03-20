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

		String act = (String) getRequestAttributes().get("act");
		String status = (String) getRequestAttributes().get("status");

		if (act.equalsIgnoreCase("4")) {
			// TODO: change 1
			processLayer(status);
			getResponse().redirectSeeOther("/jUCP/approach/4");
			return "/jUCP/approach/" + act + "/" + status;

		} else if (act.equalsIgnoreCase("5")) {
			// TODO: change 2
			processActor(status);
			getResponse().redirectSeeOther("/jUCP/approach/5");
			return "/jUCP/approach/" + act + "/" + status;

		} else if (act.equalsIgnoreCase("6")) {
			// TODO: change 3
			processMediate(status);
			getResponse().redirectSeeOther("/jUCP/approach/6");
			return "/jUCP/approach/" + act + "/" + status;

		} else {
			return "";

		}

	}

	private void processMediate(String status) {

		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ruleFile = context.getAttribute("rules").toString()
				+ "userpolicy_" + status + ".rules";
		Model model = ModelFactory.createDefaultModel();
		Resource configuration = model.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet, ruleFile);
		Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(
				configuration);

		String ontology = context.getAttribute("ontology").toString();
		Model modelRDF = FileManager.get().loadModel(ontology + "hess.ttl");

		InfModel infModel = ModelFactory.createInfModel(reasoner, modelRDF);
		infModel.prepare();

		if (infModel != null) {
			context.setAttribute("status", "negociate");
			try {
				// output inferences to file
				File outFile = new File(ontology + "hess_negociate.ttl");
				Writer writer = new FileWriter(outFile);
				infModel.write(writer, "TURTLE");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.out.println("Exception caught" + e.getMessage());
			}
		}

	}

	private void processActor(String status) {
		// TODO 1.2.3.4.5
		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ruleFile = context.getAttribute("rules").toString()
				+ "condition_" + status + ".rules";
		Model model = ModelFactory.createDefaultModel();
		Resource configuration = model.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet, ruleFile);
		Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(
				configuration);

		String ontology = context.getAttribute("ontology").toString();
		Model modelRDF = FileManager.get().loadModel(ontology + "hess.ttl");

		InfModel infModel = ModelFactory.createInfModel(reasoner, modelRDF);
		infModel.prepare();

		if (infModel != null) {
			context.setAttribute("status", "actor");
			try {
				// output inferences to file
				File outFile = new File(ontology + "hess_actor.ttl");
				Writer writer = new FileWriter(outFile);
				infModel.write(writer, "TURTLE");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.out.println("Exception caught" + e.getMessage());
			}
		}

	}

	private void processLayer(String status) {
		// TODO 1.2.3
		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ruleFile = context.getAttribute("rules").toString()
				+ "abstraction_" + status + ".rules";
		Model model = ModelFactory.createDefaultModel();
		Resource configuration = model.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet, ruleFile);
		Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(
				configuration);

		String ontology = context.getAttribute("ontology").toString();
		Model modelRDF = FileManager.get().loadModel(ontology + "hess.ttl");

		InfModel infModel = ModelFactory.createInfModel(reasoner, modelRDF);
		infModel.prepare();

		if (infModel != null) {
			context.setAttribute("status", "layer");
			try {
				// output inferences to file
				File outFile = new File(ontology + "hess_layer.ttl");
				Writer writer = new FileWriter(outFile);
				infModel.write(writer, "TURTLE");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.out.println("Exception caught" + e.getMessage());
			}

		}
	}

}
