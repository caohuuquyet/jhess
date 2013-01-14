package eu.tsp.hess;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;


public class JenaRules {

	public static void main(String[] args) {
		//String rdfFile 
		String rdfFile = "rdf/hess2.ttl";
		//String ruleFile 
		String ruleFile = "rules/hess2.rules";
		
		
		// create a default model (empty model)
	      Model model = ModelFactory.createDefaultModel();
			
	      // create a resource (empty resource)
	      Resource configuration = model.createResource();
			
	      // set engine mode
	      configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
	 
	      // set the rules file
	      configuration.addProperty(ReasonerVocabulary.PROPruleSet,  ruleFile);
			
	      // Create an instance of such a reasoner
	      Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
			
	      // Load RDF test data
	      model = FileManager.get().loadModel(rdfFile);
			
	      // create the inference model
	      InfModel infModel = ModelFactory.createInfModel(reasoner, model);
			
	      // force starting the rule execution
	      infModel.prepare();
			
	      // write down the results 
	      infModel.write(System.out, "N3");

	}

}
