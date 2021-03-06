package com.orange.ucp;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;




/**
 * Application Lifecycle Listener implementation class SmartHomeContextListener
 *
 */
public class SmartHomeContextListener implements ServletContextListener, ServletContextAttributeListener, 
HttpSessionListener
{

    /**
     * Default constructor. 
     */
    public SmartHomeContextListener() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Read Ontology
		ServletContext	sc = sce.getServletContext(); 
		String ontology = sc.getRealPath("/WEB-INF") + sc.getInitParameter("ontology");
		ontology = ontology.replaceAll("\\\\+", "/");
		String rules = sc.getRealPath("/WEB-INF") + sc.getInitParameter("rules");
		rules = rules.replaceAll("\\\\+", "/");
		String minsup = sc.getInitParameter("minsup");
		String status = sc.getInitParameter("status");
		
		
		sc.setAttribute("ontology", ontology);
		sc.setAttribute("rules", rules);
		sc.setAttribute("minsup", minsup);
		sc.setAttribute("status", status);
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Destroy Ontology 
	
		ServletContext	sc = sce.getServletContext(); 
		
		//save ontology to database
		
		//remove ontology in memeory
		sc.removeAttribute("ontology"); 		
		System.out.println("ontology Destroyed:" + sc.getAttribute("ontology") );
		
		
	}


	@Override
	public void attributeAdded(ServletContextAttributeEvent scae) {
		// TODO Auto-generated method stub
		System.out.println(scae.getName() + " Add:" + scae.getValue() );
		
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent scae) {
		// TODO Auto-generated method stub
		System.out.println(scae.getName() + " Remove:" + scae.getValue() );
		
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent scae) {
		// TODO Auto-generated method stub
		System.out.println(scae.getName() + " Replace:" + scae.getValue() );
		
		
	}

	@Override
	public void sessionCreated(HttpSessionEvent hse) {
		// TODO Auto-generated method stub
		ServletContext	sc = hse.getSession().getServletContext();
		
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
