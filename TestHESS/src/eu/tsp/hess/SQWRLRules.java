package eu.tsp.hess;

import java.io.FileInputStream;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import org.protege.owl.portability.query.LiteralValue;
import org.protege.owl.portability.query.Result;
import org.protege.owl.portability.query.ResultException;
import org.protege.swrlapi.core.SWRLRuleEngine;
import org.protege.swrlapi.exceptions.SWRLRuleEngineException;
import org.protege.swrlapi.sqwrl.SQWRLQueryEngine;
import org.protege.swrltab.p3.P3SQWRLQueryEngineFactory;
import org.protege.swrltab.p3.P3SWRLRuleEngineFactory;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class SQWRLRules {

	public static void main(String[] args) { 

		String sqwrl = "Location(?l) -> sqwrl:select(?l)";

		String uri = "C:/Users/quyet_ca.MICRO.000/workspace/TestHESS/rdf/hess2.ttl"; 

		try {
			FileInputStream is;
            
            is = new FileInputStream(uri);
           
            JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(is);
			System.out.print("D" + owlModel);
			
			SQWRLQueryEngine queryEngine = P3SQWRLQueryEngineFactory.create(owlModel);

			Result result = queryEngine.runSQWRLQuery("Query-1",sqwrl);

			while (result.hasNext()) {
				LiteralValue nameValue = result.getLiteralValue("?l");
				
				System.out.println("Name: " + nameValue.getString());
				

				result.next();
			}
		} catch (Exception e) {

		}

	}

}
