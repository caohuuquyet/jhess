package eu.tsp.hess;

import java.io.IOException;
import java.io.PrintWriter;

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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

/*import java.util.ArrayList;
import java.util.Collection;
import com.hp.hpl.jena.util.FileUtils;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.*;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.IndividualValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLResult;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;*/

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

		// Define sparql query string

		PrintWriter out = response.getWriter();

		
		// processSPARQL(out, request);
		processJenaRules(out, request);
		// processSWRLRules(out, request);
		// processSQWRL(out, request);


	}

	

	private void processSPARQL(PrintWriter out, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String queryString = ""
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>  "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX : <http://www.hess.tsp.eu/2013/1/Maisel.owl#>   "

				+ "SELECT ?name  "
				+ "WHERE { ?subject rdf:type :LightingDevice ."
				+ " ?subject :hasName ?name. " + " ?subject :hasValue 'ON'. }"
				+ "ORDER BY ASC(?name)"; 

		// Query
		Object model = request.getSession().getServletContext()
				.getAttribute("ontology");
		Query query = QueryFactory.create(queryString);

		QueryExecution qe = QueryExecutionFactory.create(query, (Model) model);
		ResultSet results = qe.execSelect();

		// Print out as a table
		while (results.hasNext()) {
			QuerySolution row = results.next();
			RDFNode subject = row.get("name");
			out.write("Lamp t: " + subject.toString() + "\n");

		}

		qe.close();

	}
	
	private void processJenaRules(PrintWriter out, HttpServletRequest request) {
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
		infModel.write(out, "N3");

	}
	
	
	private void processSWRLRules(PrintWriter out, HttpServletRequest request) {
		// TODO Auto-generated method stub
		

	}
	
	private void processSQWRL(PrintWriter out, HttpServletRequest request) {
		// TODO http://protege.cim3.net/cgi-bin/wiki.pl?SQWRLQueryAPI
		
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
