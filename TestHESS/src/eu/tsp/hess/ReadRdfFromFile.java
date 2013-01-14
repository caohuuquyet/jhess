package eu.tsp.hess;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class ReadRdfFromFile {


	public static void main(String[] args) {

		//Load RDF from file to Jena Model
		String rdfFile = "rdf/hess.ttl";
		Model model = FileManager.get().loadModel(rdfFile);
		
		//Print out
		System.out.println("============== N3 Format =================");
		//model.write(System.out,"N3");
		
		System.out.println();
		System.out.println("============== RDF/XML Format =================");
		model.write(System.out,"RDF/XML");
	}

}
