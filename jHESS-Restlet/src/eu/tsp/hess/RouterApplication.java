package eu.tsp.hess;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.LocalReference;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

public class RouterApplication extends Application {
	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		// Create a router Restlet that routes each call to a new respective
		// instance of resource.
		Router router = new Router(getContext());

		Directory dir = new Directory(getContext(), new Reference("war:///view/images"));
       
		router.attach("/view/images", dir);
		

		// Defines only two routes
		router.attach("/users", UserResource.class);
		router.attach("/users/{uid}", UserResource.class);
		router.attach("/users/{uid}/items", UserItemResource.class);
		router.attach("/rdf", TestRDFResource.class);
		router.attach("/html", TestHTMLResource.class);
		router.attach("/json", TestJSONResource.class);
		return router;
	}
}