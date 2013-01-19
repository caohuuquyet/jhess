package eu.tsp.hess;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

public class RouterApplication extends Application {
	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		
		Router router = new Router(getContext());

		Directory dir = new Directory(getContext(), new Reference(
				"war:///view/images"));

		router.attach("/view/images", dir);

		router.attach("/approach/{act}", ApproachResource.class);
		router.attach("/device/{did}", DeviceResource.class);
		router.attach("/device/{did}/{status}", DeviceStatusResource.class);

		return router;
	}
}