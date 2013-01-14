package eu.tsp.hess;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;


public class SparqlFromSesame {

	public static void main(String[] args) {
		//Sesame sparql enpoint
		String sparqlEndPoint = "http://192.41.170.153:8080/openrdf-sesame/repositories/test";
		
		//Define sparql query string
		String queryString = ""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
				+ "PREFIX ex: <http://www.example.org/> "
				
				+ "SELECT  *  "
				
				+ "WHERE "
				+ "{"
				+ "	ex:sun ex:satellite ?planet .  "
				+ "}"
				+ "" ;
		
		//Execute Query
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndPoint, query);
		ResultSet results = qe.execSelect(); 
		
		//Print out result
		ResultSetFormatter.out(System.out, results, query);
		
		qe.close();
	}

}
