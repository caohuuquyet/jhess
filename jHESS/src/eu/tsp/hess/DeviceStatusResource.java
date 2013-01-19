package eu.tsp.hess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com.hp.hpl.jena.util.PrintUtil;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class DeviceStatusResource extends ServerResource {

	@Get
	public String processStatus() throws JSONException {
		String did = (String) getRequestAttributes().get("did");
		String status = (String) getRequestAttributes().get("status");

		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		Object modelRDF = context.getAttribute("ontology");

		String ruleFile = context.getAttribute("rules").toString()
				+ "temp.rules";

		StringBuilder sb = new StringBuilder();

		if (status.equalsIgnoreCase("on")) {
			sb.append("[r1: (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"OFF\"^^xsd:string ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"ON\"^^xsd:string ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
		} else {
			sb.append("[r2:   (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"ON\"^^xsd:string ), noValue(?c jhess:ruleFiredFor jhess:jhessv) -> remove(0), (jhess:"
					+ did
					+ " jhess:hasCurrentDeviceStatus \"OFF\"^^xsd:string ), (?c jhess:ruleFiredFor jhess:jhessv),	hide(jhess:ruleFiredFor)] ");
		}

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
			bw.write("@prefix jhess: <http://www.hess.tsp.eu/2013/1/Maisel.owl#>.");
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

		// Create inferred model using the reasoner and write it out.
		InfModel infModel = ModelFactory.createInfModel(reasoner,
				(Model) modelRDF);
		infModel.prepare();

		if (infModel != null) {
			context.setAttribute("ontology", infModel);
			// infModel.write(System.out);
		}

		return did + ":" + status + sb.toString();

	}
}
