package eu.tsp.hess;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;


public class SparqlWithOntology {

	public static void main(String[] args) {
		//Load RDF file to Jena model
		String rdfFile = "rdf/ex1.ttl";
		Model model = FileManager.get().loadModel(rdfFile);
		
		//Create OWL ontology
		Reasoner owl = ReasonerRegistry.getOWLReasoner();
		
		//Inference the jena model with OWL
		InfModel infmodel = ModelFactory.createInfModel(owl, model);
		
		//Define sparql query string
		String queryString = ""
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
				+ "PREFIX act: <http://www.activity.com/>  "
				
				+ "SELECT ?parent ?child "
				
				+ "WHERE "
				+ "{"
				//+ "	?parent act:hasChild ?child .  "
				+ "	?child act:hasParent ?parent .  "
				+ "}"
				+ "" ;
		
		//Execute query
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, infmodel);
		ResultSet results =  qe.execSelect();
		
		//Print out result
		ResultSetFormatter.out(System.out, results, query);

	    qe.close();
		
	}

}
