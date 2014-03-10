package com.orange.ucp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.orange.ucp.dto.Activity;
import com.orange.ucp.dto.Device;

import com.sun.xml.internal.ws.api.pipe.NextAction;

import freemarker.template.Configuration;

public class ApproachResource extends ServerResource {

	@Get("html")
	public Representation toHTML() throws ResourceException {

		String act = (String) getRequestAttributes().get("act");

		if (act.equalsIgnoreCase("1")) {
			return processApproach1();
		} else if (act.equalsIgnoreCase("2")) {
			return processApproach2();
		} else if (act.equalsIgnoreCase("3")) {
			return processApproach3();
		} else if (act.equalsIgnoreCase("4")) {
			return processApproach4();
		} else if (act.equalsIgnoreCase("5")) {
			return processApproach5();
		} else if (act.equalsIgnoreCase("6")) {
			return processApproach6();
		} else {
			return processApproach3Detail(act);
		}

	}

	
	@Get("rdf")
	public Representation toRDF() throws ResourceException {
		String act = (String) getRequestAttributes().get("act");
		String rdfTemplate = "";

		Configuration cfg = new Configuration();

		ContextTemplateLoader loader = new ContextTemplateLoader(getContext(),
				"war:///WEB-INF");

		cfg.setTemplateLoader(loader);

		TemplateRepresentation rep = null;
		final Map<String, Object> dataModel = new TreeMap<String, Object>();
		// dataModel.put("devices", devices);

		if (act.equalsIgnoreCase("1")) {
			rdfTemplate = "rdf/hess.ttl";
			rep = new TemplateRepresentation(rdfTemplate, cfg, dataModel,
					MediaType.APPLICATION_RDF_TURTLE);

		} else if (act.equalsIgnoreCase("2")) {
			rdfTemplate = "rules/hess.rules";
			rep = new TemplateRepresentation(rdfTemplate, cfg, dataModel,
					MediaType.TEXT_PLAIN);

		} else if (act.equalsIgnoreCase("3")) {
			rdfTemplate = "rdf/datacloud.ttl";
			rep = new TemplateRepresentation(rdfTemplate, cfg, dataModel,
					MediaType.APPLICATION_RDF_TURTLE);

		}

		return rep;

	}

	private Representation processApproach3Detail(String act)
			throws ResourceException {
		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?id ?value ?time"
				+ " WHERE {"
				+ " ?id jhess:hasActivity jhess:"
				+ act
				+ ". ?id jhess:hasActivityValue ?value. ?id jhess:hasActivityTime ?time."
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

		String input = context.getAttribute("ontology").toString()
				+ "pattern.ttl";
		convertToFile(activities, input);

		String output = context.getAttribute("rules").toString()
				+ "pattern.rules";
		String result = "";

		double minsup = Double.parseDouble(context.getAttribute("minsup")
				.toString()); // means a minsup of 2 transaction (we used a
		// relative support)

		// Applying the Apriori algorithm
		PatternMining_SaveToFile apriori = new PatternMining_SaveToFile();
		try {
			apriori.runAlgorithm(minsup, input, output, act);
			// Read in the file into a list of strings
			BufferedReader reader = new BufferedReader(new FileReader(output));

			String line = reader.readLine();

			while (line != null) {
				result += line + "<br/>";
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		dataModel.put("temppattern",
				" <br/><b>Pattern Mining for Turning OFF device " + act
						+ " (MinSup =" + minsup * 100 + "%):</b> <br/>"
						+ result);

		rep = new TemplateRepresentation("patterndetail.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;
	}

	private void convertToFile(List<Activity> list, String fileName) {
		// TODO

		// convert list to map
		TreeMap<String, String> map = new TreeMap<String, String>();
		Iterator<Activity> itr = list.iterator();

		while (itr.hasNext()) {
			Activity act = (Activity) itr.next();
			String time = act.getTime();
			String day = time.substring(0, 10);
			String h = time.substring(11, 13);
			String m = time.substring(14, 16);
			// int minutes = m % 15 < 8 ? m / 15 * 15 : (m / 15 + 1) * 15;

			if (act.getValue().equalsIgnoreCase("off")) {
				if (map.containsKey(day)) {
					// update
					String newValue = h + "" + m;
					String oldValue = map.get(day);

					if (oldValue.indexOf(newValue) >= 0) {
						//System.out.print("nothing");

					} else {
						String updateValue = oldValue + " " + h + "" + m;
						map.remove(day);
						map.put(day, updateValue);
					}
				} else {
					// add
					map.put(day, h + "" + m);
				}
			}

		}// end of while

		// save treemap to file
		Set<Map.Entry<String, String>> set = map.entrySet();
		String p = new String();
		// String p = "";
		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter fOut = new BufferedWriter(fw);

			for (Map.Entry<String, String> me : set) {
				p = me.getValue();
				fOut.write(p);
				fOut.newLine();
			}
			fOut.close();
		} catch (IOException e) {
			return;
		}

	}

	private Representation processApproach1() throws ResourceException {
		// TODO Auto-generated method stub

		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?id ?description ?location ?inputpower ?unit ?status ?start ?datacloud"
				+ " WHERE {"
				+ " ?id jhess:hasDescription ?description. ?id jhess:hasLocation ?location. ?id jhess:hasInputPower ?inputpower. ?id jhess:hasInputPowerUnit  ?unit. ?id jhess:hasCurrentDeviceStatus ?status.?id jhess:hasStatusStartTime ?start. "
				+ " OPTIONAL { ?id jhess:hasCurrentMeasureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentTemperatureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentPresenceValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentHumidityValue ?datacloud }}"
				+ " ORDER BY ASC(?id)";
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
				if (binding.getLiteral("datacloud") != null) {
					result.setDataCloud(binding.getLiteral("datacloud")
							.getString());
				} else {
					result.setDataCloud(new String(""));

				}

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

		getResponse().redirectSeeOther("/jUCP/approach/1");

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
				String id = binding.getResource("id").getLocalName();
				result.setId(id);

				String device = binding.getResource("device").getLocalName();
				result.setDevice(device);

				result.setValue(binding.getLiteral("value").getString());
				result.setTime(binding.getLiteral("time").getString());

				if ((device.indexOf("sensor") >= 0)
						|| (device.indexOf("meter") >= 0)) {

				} else {
					activities.add(result);
				}
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

	private Representation processApproach4() throws ResourceException {
		// TODO Auto-generated method stub

		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?id ?description ?location ?inputpower ?unit ?status ?start ?datacloud"
				+ " WHERE {"
				+ " ?id jhess:hasDescription ?description. ?id jhess:hasLocation ?location. ?id jhess:hasInputPower ?inputpower. ?id jhess:hasInputPowerUnit  ?unit. ?id jhess:hasCurrentDeviceStatus ?status.?id jhess:hasStatusStartTime ?start. "
				+ " OPTIONAL { ?id jhess:hasCurrentMeasureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentTemperatureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentPresenceValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentHumidityValue ?datacloud }}"
				+ " ORDER BY ASC(?id)";
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
				if (binding.getLiteral("datacloud") != null) {
					result.setDataCloud(binding.getLiteral("datacloud")
							.getString());
				} else {
					result.setDataCloud(new String(""));

				}

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

		rep = new TemplateRepresentation("abstraction.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;

	}
	
	private Representation processApproach5() throws ResourceException {
		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?id ?description ?location ?inputpower ?unit ?status ?start ?datacloud"
				+ " WHERE {"
				+ " ?id jhess:hasDescription ?description. ?id jhess:hasLocation ?location. ?id jhess:hasInputPower ?inputpower. ?id jhess:hasInputPowerUnit  ?unit. ?id jhess:hasCurrentDeviceStatus ?status.?id jhess:hasStatusStartTime ?start. "
				+ " OPTIONAL { ?id jhess:hasCurrentMeasureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentTemperatureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentPresenceValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentHumidityValue ?datacloud }}"
				+ " ORDER BY ASC(?id)";
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
				if (binding.getLiteral("datacloud") != null) {
					result.setDataCloud(binding.getLiteral("datacloud")
							.getString());
				} else {
					result.setDataCloud(new String(""));

				}

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

		rep = new TemplateRepresentation("condition.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;
		
	}	
	
	private Representation processApproach6() throws ResourceException {
		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?id ?description ?location ?inputpower ?unit ?status ?start ?datacloud"
				+ " WHERE {"
				+ " ?id jhess:hasDescription ?description. ?id jhess:hasLocation ?location. ?id jhess:hasInputPower ?inputpower. ?id jhess:hasInputPowerUnit  ?unit. ?id jhess:hasCurrentDeviceStatus ?status.?id jhess:hasStatusStartTime ?start. "
				+ " OPTIONAL { ?id jhess:hasCurrentMeasureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentTemperatureValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentPresenceValue ?datacloud } OPTIONAL { ?id jhess:hasCurrentHumidityValue ?datacloud }}"
				+ " ORDER BY ASC(?id)";
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
				if (binding.getLiteral("datacloud") != null) {
					result.setDataCloud(binding.getLiteral("datacloud")
							.getString());
				} else {
					result.setDataCloud(new String(""));

				}

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

		rep = new TemplateRepresentation("mediation.html", cfg, dataModel,
				MediaType.TEXT_HTML);

		return rep;
		
	}
}