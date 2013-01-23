package eu.tsp.hess;

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

public class DeviceStatusResource extends ServerResource {

	@Get
	public String processStatus() throws JSONException {

		String did = (String) getRequestAttributes().get("did");
		String status = (String) getRequestAttributes().get("status");
		Date now = new Date();

		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss");
		String dateTime = dateFormatter.format(now);

		StringBuilder sb = new StringBuilder();

		if (status.equalsIgnoreCase("on")) {
			sb.append("[r1: (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"OFF\"^^xsd:string ), (jhess:"
					+ did
					+ " jhess:hasStatusStartTime ?time ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0),remove(1) (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"ON\"^^xsd:string ),  (jhess:"
					+ did
					+ " jhess:hasStatusStartTime \""+ dateTime +"\" ),(?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");

		} else if (status.equalsIgnoreCase("off")) {
			sb.append("[r1: (jhess:" 
					+ did
					+ " jhess:hasCurrentDeviceStatus \"ON\"^^xsd:string ), (jhess:"
					+ did
					+ " jhess:hasStatusStartTime ?time ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0),remove(1) (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"OFF\"^^xsd:string ),  (jhess:"
					+ did
					+ " jhess:hasStatusStartTime \""+ dateTime +"\" ),(?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");

		} else {
			if (did.contains("sensor_temperature")) {
				sb.append("[r3: (jhess:"
						+ did
						+ " jhess:hasCurrentTemperatureValue ?value ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
						+ did
						+ " jhess:hasCurrentTemperatureValue "
						+ status
						+ " ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
			} else if (did.contains("sensor_humidity")) {
				sb.append("[r4: (jhess:"
						+ did
						+ " jhess:hasCurrentHumidityValue ?value ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
						+ did
						+ " jhess:hasCurrentHumidityValue "
						+ status
						+ " ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
			} else if (did.contains("sensor_presence")) {
				sb.append("[r5: (jhess:"
						+ did
						+ " jhess:hasCurrentPresenceValue ?value ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
						+ did
						+ " jhess:hasCurrentPresenceValue \""
						+ status
						+ "\"^^xsd:boolean ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
			}  else if (did.contains("meter")) {
				sb.append("[r6: (jhess:"
						+ did
						+ " jhess:hasCurrentMeasureValue ?value ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
						+ did
						+ " jhess:hasCurrentMeasureValue \""
						+ status
						+ "\"^^xsd:int ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
			}
		}

		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ruleFile = context.getAttribute("rules").toString()
				+ "temp.rules";

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
			bw.write("@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.");
			bw.newLine();
			bw.write("@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#>.");
			bw.newLine();
			bw.write("@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.");
			bw.newLine();
			bw.write("@prefix owl:	<http://www.w3.org/2002/07/owl#>.");
			bw.newLine();
			bw.write("@prefix jhess: <http://jhess.googlecode.com/files/jhess.owl#>.");
			bw.newLine();
			bw.write(sb.toString());
			bw.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

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

		addToDataCloud(dateTime, did, status);
		// add this triple to datacloud
		getResponse().redirectSeeOther("/jHESS/approach/1");
		return dateTime + "," + did + "," + status;

	}

	private void addToDataCloud(String dateTime, String did, String status) {

		StringBuilder sb = new StringBuilder();
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss");

		String time = "";
		try {
			time = "" + dateFormatter.parse(dateTime).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}

		sb.append("[add1: -> (jhess:" + did + "_" + time
				+ " rdf:type jhess:DataCloud),(jhess:" + did + "_" + time
				+ " rdf:type owl:NamedIndividual), " + "(jhess:" + did + "_"
				+ time + " jhess:hasActivity jhess:" + did + "), (jhess:" + did
				+ "_" + time + " jhess:hasActivityValue \"" + status
				+ "\"^^xsd:string), (jhess:" + did + "_" + time
				+ " jhess:hasActivityTime \"" + dateTime + "\"^^xsd:dateTime)" +

				"] ");

		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ruleFile = context.getAttribute("rules").toString()
				+ "temp.rules";

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
			bw.write("@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.");
			bw.newLine();
			bw.write("@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#>.");
			bw.newLine();
			bw.write("@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.");
			bw.newLine();
			bw.write("@prefix owl:	<http://www.w3.org/2002/07/owl#>.");
			bw.newLine();
			bw.write("@prefix jhess: <http://jhess.googlecode.com/files/jhess.owl#>.");
			bw.newLine();
			bw.write(sb.toString());
			bw.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

		Model model = ModelFactory.createDefaultModel();
		Resource configuration = model.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet, ruleFile);
		Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(
				configuration);

		String ontology = context.getAttribute("ontology").toString();
		Model modelRDF = FileManager.get()
				.loadModel(ontology + "datacloud.ttl");
		InfModel infModel = ModelFactory.createInfModel(reasoner, modelRDF);
		infModel.prepare();

		if (infModel != null) {
			context.setAttribute("ontology", ontology);
			try {
				// output inferences to file
				File outFile = new File(ontology + "datacloud.ttl");
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
