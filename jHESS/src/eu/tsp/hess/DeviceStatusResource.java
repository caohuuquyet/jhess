package eu.tsp.hess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

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
		String time = "";

		StringBuilder sb = new StringBuilder();

		if (status.equalsIgnoreCase("on")) {
			sb.append("[r1: (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"OFF\"^^xsd:string ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"ON\"^^xsd:string ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
		} else if (status.equalsIgnoreCase("off")) {
			sb.append("[r2:   (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"ON\"^^xsd:string ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"OFF\"^^xsd:string ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
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
		Model modelRDF = FileManager.get().loadModel(ontology);
		InfModel infModel = ModelFactory.createInfModel(reasoner, modelRDF);
		infModel.prepare();

		if (infModel != null) {
			context.setAttribute("ontology", ontology);
			try {
				// output inferences to file
				File outFile = new File(ontology);
				Writer writer = new FileWriter(outFile);
				infModel.write(writer, "TURTLE");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.out.println("Exception caught" + e.getMessage());
			}
		}

		return did + ":" + status + ruleFile;

	}
}
