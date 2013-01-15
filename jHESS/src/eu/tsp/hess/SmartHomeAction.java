package eu.tsp.hess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import eu.tsp.hess.dto.*;

/**
 * Servlet implementation class SmartHomeAction
 */
public class SmartHomeAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SmartHomeAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}

	/**
	 * Processes requests for both HTTP
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String _action = request.getParameter("act");
		_action = (_action == null) ? "sparql" : _action;

		if (_action.equalsIgnoreCase("jenarules")) {
			processJenaRules(response, request);
		} else if  (_action.equalsIgnoreCase("swrlrules")) {
			processSWRLRules(response, request);
		} else if  (_action.equalsIgnoreCase("swrlrules")) {
			processSQWRL(response, request);
		}
		
		processSPARQL(response, request);		
	}

	
	private void processSPARQL(HttpServletResponse response,
			HttpServletRequest request) {
		// TODO
		String queryString = ""
				+ "PREFIX : <http://www.hess.tsp.eu/2013/1/Maisel.owl#>"
				+ "SELECT ?id ?description ?location ?inputpower ?unit ?status ?startime ?datacloud"
				+ "WHERE {"
				+ " ?id :hasDescription ?description. ?id :hasLocation ?location. ?id :hasInputPower ?inputpower. ?id :hasInputPowerUnit  ?unit. ?id :hasCurrentDeviceStatus ?status.?id :hasStatusStartTime ?startime. ?id :hasHistoryData ?datacloud."
				+ "}" + "ORDER BY ASC(?id)";
		QueryExecution qe = null;
		// Query
		try {

			Object model = request.getSession().getServletContext()
					.getAttribute("ontology");
			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, (Model) model);
			ResultSet rs = qe.execSelect();

			List<Device> results = new ArrayList<Device>();
			while (rs.hasNext()) {
				Device result = new Device();
				QuerySolution binding = rs.nextSolution();
				result.setId(binding.getResource("id").getLocalName());
				result.setDescription(binding.getLiteral("description").getString());
				result.setLocation(binding.getResource("location").getLocalName());
				result.setInputPower(binding.getLiteral("inputpower").getInt());
				result.setInputPowerUnit(binding.getLiteral("unit").getString());
				result.setCurrentDeviceStatus(binding.getLiteral("status")
						.getString());
				
				results.add(result);
			}
 
			request.setAttribute("devices", results);
			RequestDispatcher rd = request
					.getRequestDispatcher("/view/smarthome.jsp");
			rd.forward(request, response);
		} catch (Exception e) {

		} finally {
			qe.close();
		}

	}

	private void processJenaRules(HttpServletResponse out,
			HttpServletRequest request) {
		// TODO Auto-generated method stub//String rdfFile

		String ruleFile = request.getSession().getServletContext()
				.getAttribute("rules").toString();
		Model model = ModelFactory.createDefaultModel();
		Resource configuration = model.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet, ruleFile);
		Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(
				configuration);

		Object modelRDF = request.getSession().getServletContext()
				.getAttribute("ontology");
		InfModel infModel = ModelFactory.createInfModel(reasoner,
				(Model) modelRDF);
		infModel.prepare();
		if (infModel != null) {
			// update ontology 
			request.getSession().getServletContext()
					.setAttribute("ontology", infModel);
		}

	}

	private void processSWRLRules(HttpServletResponse response, HttpServletRequest request) {
		// TODO Auto-generated method stub

	}

	private void processSQWRL(HttpServletResponse response,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}
