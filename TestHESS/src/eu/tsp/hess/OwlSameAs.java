package eu.tsp.hess;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;


public class OwlSameAs {


	public static void main(String[] args) {
		
		//Define URL of RDF
		String url = "http://dbpedia.org/data/Roger_Federer.rdf";
		
		//Load RDF from file to Jena model
		Model model = ModelFactory.createDefaultModel();
		model.read(url);
		
		//Filter RDF triples
		StmtIterator sts = model.listStatements((Resource)null, OWL.sameAs, (RDFNode)null);
		
		//Process each RDF triple
		while (sts.hasNext()){
			Statement st = sts.nextStatement();
			
			System.out.print(st.getSubject().getURI() + " ");
			System.out.print(st.getPredicate().getURI() + " ");
			System.out.println(st.getObject());
		}

	}

}
