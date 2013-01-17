package eu.tsp.hess;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import com.sun.jersey.api.view.Viewable;

@Path("autorule")
public class RuleAction {
	@javax.ws.rs.core.Context
	ServletContext context;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response processJenaRules() {
		// TODO Auto-generated method stub//String rdfFile

		String ruleFile = context.getAttribute("rules").toString();
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
		
		return Response.ok(new Viewable("/policy")).build();

	}

}
