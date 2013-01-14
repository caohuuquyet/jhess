package eu.tsp.hess;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;


public class SparqlFromFile {

	public static void main(String[] args) {
		//Load RDF file to Jena model
		String rdfFile = "rdf/ex1.ttl";
		Model model = FileManager.get().loadModel(rdfFile);
		
		//Define sparql query string
		String queryString = ""
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
				+ "PREFIX act: <http://www.activity.com/>  "
				
				+ "SELECT ?parent ?child "
				
				+ "WHERE "
				+ "{"
				+ "	?parent act:hasChild ?child .  "
				//+ "	?child act:hasParent ?parent .  "
				+ "}"
				+ "" ;
		
		//Query
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		
		//Print out as a table
		ResultSetFormatter.out(System.out, results, query);

	    qe.close();
		
	}
}
