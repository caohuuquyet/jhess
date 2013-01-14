package eu.tsp.hess;

import java.io.File;
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
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;

public class SQWRLRules {

	public static void main(String[] args) { 

		String sqwrl = "Location(?p)-> sqwrl:select(?p)";
		
		 String uri = "file:/C:/Users/quyet_ca.MICRO.000/workspace/TestHESS/rdf/hess2.ttl";
			  

		
		try {       
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
			LocalFolderRepository rep = new LocalFolderRepository(new
			File("C://Users//quyet_ca.MICRO.000//workspace//TestHESS//rdf"),true);
			owlModel.getRepositoryManager().addGlobalRepository(rep);
			owlModel.getNamespaceManager().setDefaultNamespace(uri);
		
              
			System.out.print("D:" + owlModel);
			
			SQWRLQueryEngine queryEngine = P3SQWRLQueryEngineFactory.create(owlModel);
			
			queryEngine.createSQWRLQuery("Query-1",sqwrl);

			Result result = queryEngine.runSQWRLQuery("Query-1");

			while (result.hasNext()) {
				LiteralValue nameValue = result.getLiteralValue("?l");
				
				System.out.println("Name: " + nameValue.getString());
				

				result.next();
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());

		}

	}

}
