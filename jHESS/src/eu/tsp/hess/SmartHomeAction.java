package eu.tsp.hess;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import com.sun.jersey.api.view.Viewable;

import eu.tsp.hess.dto.Device;

@Path("devices")
public class SmartHomeAction {
	@javax.ws.rs.core.Context
	ServletContext context;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response index() {

		String queryString = ""
				+ "PREFIX : <http://www.hess.tsp.eu/2013/1/Maisel.owl#>"
				+ "SELECT ?id ?description ?location ?inputpower ?unit ?status ?startime ?datacloud"
				+ "WHERE {"
				+ " ?id :hasDescription ?description. ?id :hasLocation ?location. ?id :hasInputPower ?inputpower. ?id :hasInputPowerUnit  ?unit. ?id :hasCurrentDeviceStatus ?status.?id :hasStatusStartTime ?startime. ?id :hasHistoryData ?datacloud."
				+ "}" + "ORDER BY ASC(?id)";
		QueryExecution qe = null;
		List<Device> results = new ArrayList<Device>();
		// Query
		try {

			Object model = context.getAttribute("ontology");
			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, (Model) model);
			ResultSet rs = qe.execSelect();

			results = new ArrayList<Device>();
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

				results.add(result);
			}

		} catch (Exception e) {

		} finally {
			qe.close();
		}

		return Response.ok(new Viewable("/smarthome", results)).build();
	}

	/*
	 * @Produces(MediaType.TEXT_XML) public String sayHelloInXML() { return
	 * "<html> " + "<title>" + "Hello world!" + "</title>" + "<body><h1>" +
	 * "Hello world!" + "</body></h1>" + "</html> "; }
	 */
}