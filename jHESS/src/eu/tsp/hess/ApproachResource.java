package eu.tsp.hess;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import eu.tsp.hess.dto.Activity;
import eu.tsp.hess.dto.Device;
import freemarker.template.Configuration;

public class ApproachResource extends ServerResource {

	@Get("html")
	public Representation process() throws ResourceException {

		String act = (String) getRequestAttributes().get("act");

		if (act.equalsIgnoreCase("1")) {
			return processApproach1();
		} else if (act.equalsIgnoreCase("2")) {
			return processApproach2();
		} else if (act.equalsIgnoreCase("3")) {
			return processApproach3();
		} else {
			return processApproach3Detail(act);
		}

	}

	private Representation processApproach3Detail(String act) throws ResourceException {
		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?id ?value ?time"
				+ " WHERE {"
				+ " ?id jhess:hasActivity jhess:"+act+". ?id jhess:hasActivityValue ?value. ?id jhess:hasActivityTime ?time."
				+ "}" + " ORDER BY DESC(?time) LIMIT 100";
		QueryExecution qe = null;
		List<Activity> activities = new ArrayList<Activity>();
		// Query

		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ontology = context.getAttribute("ontology").toString();
		try {

			Model modelRDF = FileManager.get().loadModel(
					ontology + "datacloud.ttl");

			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, modelRDF);
			ResultSet rs = qe.execSelect();

			while (rs.hasNext()) {
				Activity result = new Activity();
				QuerySolution binding = rs.nextSolution();
				result.setId(binding.getResource("id").getLocalName());
				result.setDevice(act);
				result.setValue(binding.getLiteral("value").getString());
				result.setTime(binding.getLiteral("time").getString());

				activities.add(result);
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			qe.close();
		}

		Configuration cfg = new Configuration();

		ContextTemplateLoader loader = new ContextTemplateLoader(getContext(),
				"war:///view");

		cfg.setTemplateLoader(loader);

		TemplateRepresentation rep = null;
		final Map<String, Object> dataModel = new TreeMap<String, Object>();
		dataModel.put("activities", activities);

		rep = new TemplateRepresentation("pattern.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;
	}

	private Representation processApproach3() throws ResourceException {

		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?id ?device ?value ?time"
				+ " WHERE {"
				+ " ?id jhess:hasActivity ?device. ?id jhess:hasActivityValue ?value. ?id jhess:hasActivityTime ?time."
				+ "}" + " ORDER BY DESC(?time) LIMIT 100";
		QueryExecution qe = null;
		List<Activity> activities = new ArrayList<Activity>();
		// Query

		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ontology = context.getAttribute("ontology").toString();
		try {

			Model modelRDF = FileManager.get().loadModel(
					ontology + "datacloud.ttl");

			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, modelRDF);
			ResultSet rs = qe.execSelect();

			while (rs.hasNext()) {
				Activity result = new Activity();
				QuerySolution binding = rs.nextSolution();
				result.setId(binding.getResource("id").getLocalName());
				result.setDevice(binding.getResource("device").getLocalName());
				result.setValue(binding.getLiteral("value").getString());
				result.setTime(binding.getLiteral("time").getString());

				activities.add(result);
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			qe.close();
		}

		Configuration cfg = new Configuration();

		ContextTemplateLoader loader = new ContextTemplateLoader(getContext(),
				"war:///view");

		cfg.setTemplateLoader(loader);

		TemplateRepresentation rep = null;
		final Map<String, Object> dataModel = new TreeMap<String, Object>();
		dataModel.put("activities", activities);

		rep = new TemplateRepresentation("pattern.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;
	}

	private Representation processApproach2() throws ResourceException {
		// TODO Auto-generated method stub
		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ruleFile = context.getAttribute("rules").toString()
				+ "hess.rules";
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
			context.setAttribute("ontology", ontology);
			try {
				// output inferences to file
				File outFile = new File(ontology + "hess.ttl");
				Writer writer = new FileWriter(outFile);
				infModel.write(writer, "TURTLE");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.out.println("Exception caught" + e.getMessage());
			}
		}

		Configuration cfg = new Configuration();

		ContextTemplateLoader loader = new ContextTemplateLoader(getContext(),
				"war:///view");

		cfg.setTemplateLoader(loader);

		TemplateRepresentation rep = null;
		final Map<String, Object> dataModel = new TreeMap<String, Object>();
		dataModel.put("policy", ruleFile);

		rep = new TemplateRepresentation("policy.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;

	}

	private Representation processApproach1() throws ResourceException {
		// TODO Auto-generated method stub
		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ "SELECT ?id ?description ?location ?inputpower ?unit ?status ?start ?datacloud"
				+ "WHERE {"
				+ " ?id jhess:hasDescription ?description. ?id jhess:hasLocation ?location. ?id jhess:hasInputPower ?inputpower. ?id jhess:hasInputPowerUnit  ?unit. ?id jhess:hasCurrentDeviceStatus ?status.?id jhess:hasStatusStartTime ?start. ?id jhess:hasHistoryData ?datacloud."
				+ "}" + "ORDER BY ASC(?id)";
		QueryExecution qe = null;
		List<Device> devices = new ArrayList<Device>();
		// Query

		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ontology = context.getAttribute("ontology").toString();
		try {

			Model modelRDF = FileManager.get().loadModel(ontology + "hess.ttl");

			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, modelRDF);
			ResultSet rs = qe.execSelect();

			while (rs.hasNext()) {
				Device result = new Device();
				QuerySolution binding = rs.nextSolution();
				result.setId(binding.getResource("id").getLocalName());
				result.setDescription(binding.getLiteral("description")
						.getString());
				result.setLocation(binding.getResource("location")
						.getLocalName());
				result.setInputPower(binding.getLiteral("inputpower").getInt());
				result.setInputPowerUnit(binding.getLiteral("unit").getString());
				result.setCurrentDeviceStatus(binding.getLiteral("status")
						.getString());
				result.setStatusStartTime(binding.getLiteral("start")
						.getString());

				devices.add(result);
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			qe.close();
		}

		Configuration cfg = new Configuration();

		ContextTemplateLoader loader = new ContextTemplateLoader(getContext(),
				"war:///view");

		cfg.setTemplateLoader(loader);

		TemplateRepresentation rep = null;
		final Map<String, Object> dataModel = new TreeMap<String, Object>();
		dataModel.put("devices", devices);

		rep = new TemplateRepresentation("smarthome.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;

	}

}