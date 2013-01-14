package eu.tsp.hess;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Hello {

	public static void main(String[] args) {
		
		//Create empty Jena model
		Model model = ModelFactory.createDefaultModel();
		
		//Examine a namespace
		String nameSpace = "http://www.hess.tsp.eu/";
		
		//Create RDF statement 
		Resource subject = model.createResource(nameSpace + "semantic_programming");
		Property property = model.createProperty(nameSpace + "says");
		subject.addProperty(property, "Hello HESS!",XSDDatatype.XSDstring);
		
		//Print result
		System.out.println("============== N3 Format =================");
		model.write(System.out,"N3");
		
		System.out.println();
		System.out.println("============== RDF/XML Format =================");
		model.write(System.out,"RDF/XML");

	}

}
