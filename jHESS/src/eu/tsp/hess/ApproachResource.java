package eu.tsp.hess;

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
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

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
			return processError();
		}

	}

	private Representation processError() throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	private Representation processApproach3() throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	private Representation processApproach2() throws ResourceException {
		// TODO Auto-generated method stub
		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");

		String ruleFile = context.getAttribute("rules").toString()+"hess.rules";
		Model model = ModelFactory.createDefaultModel();
		Resource configuration = model.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet, ruleFile);
		Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(
				configuration);

		Object modelRDF = context.getAttribute("ontology");
		InfModel infModel = ModelFactory.createInfModel(reasoner,
				(Model) modelRDF);
		infModel.prepare();

		if (infModel!=null){
			context.setAttribute("ontology", infModel);
		}
		return null;
	}

	private Representation processApproach1() throws ResourceException {
		// TODO Auto-generated method stub
		String queryString = ""
				+ "PREFIX jhess: <http://www.hess.tsp.eu/2013/1/Maisel.owl#>"
				+ "SELECT ?id ?description ?location ?inputpower ?unit ?status ?start ?datacloud"
				+ "WHERE {"
				+ " ?id jhess:hasDescription ?description. ?id jhess:hasLocation ?location. ?id jhess:hasInputPower ?inputpower. ?id jhess:hasInputPowerUnit  ?unit. ?id jhess:hasCurrentDeviceStatus ?status.?id jhess:hasStatusStartTime ?start. ?id jhess:hasHistoryData ?datacloud."
				+ "}" + "ORDER BY ASC(?id)";
		QueryExecution qe = null;
		List<Device> devices = new ArrayList<Device>();
		// Query
		try {

			ServletContext context = (ServletContext) getContext()
					.getAttributes().get(
							"org.restlet.ext.servlet.ServletContext");

			Object model = context.getAttribute("ontology");
			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, (Model) model);
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
				result.setStatusStartTime(binding.getLiteral("start").getString());

				devices.add(result);
			}

		} catch (Exception e) {

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