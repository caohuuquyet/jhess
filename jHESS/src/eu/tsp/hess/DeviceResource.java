package eu.tsp.hess;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

import eu.tsp.hess.dto.Device;
import freemarker.template.Configuration;

public class DeviceResource extends ServerResource {

	@Get("json")
	public Representation toJSON() throws JSONException {
		String did = (String) getRequestAttributes().get("did");
		Device device = getDevice(did);
		JSONObject jsonDevice = new JSONObject();

		if (device != null) {
			jsonDevice.put("id", did);
			jsonDevice.put("hasDescription", device.getDescription());
			jsonDevice.put("hasLocation", device.getLocation());
			jsonDevice.put("hasInputPower", device.getInputPower());
			jsonDevice.put("hasInputPowerUnit", device.getInputPowerUnit());
			jsonDevice.put("hasCurrentDeviceStatus",
					device.getCurrentDeviceStatus());
			jsonDevice.put("hasStatusStartTime", device.getStatusStartTime());
		}
		return new JsonRepresentation(jsonDevice);
	}

	@Get("rdf")
	public Representation toRdf() throws ResourceException {
		String did = (String) getRequestAttributes().get("did");
		Device device = getDevice(did);
		String template = "";
		if (did.contains("meter")) {
			template = "meter.rdf";
		} else if (did.contains("heating")) {
			template = "heatingdevice.rdf";
		} else if (did.contains("humidity")) {
			template = "humiditysensor.rdf";
		} else if (did.contains("iPad")) {
			template = "iPad.rdf";
		} else if (did.contains("laptop")) {
			template = "laptop.rdf";
		} else if (did.contains("light")) {
			template = "lightingdevice.rdf";
		} else if (did.contains("presence")) {
			template = "presencesensor.rdf";
		} else if (did.contains("temperature")) {
			template = "temperaturesensor.rdf";
		} else if (did.contains("vent")) {
			template = "vent.rdf";
		}
		Configuration cfg = new Configuration();

		ContextTemplateLoader loader = new ContextTemplateLoader(getContext(),
				"war:///WEB-INF/rdf");

		cfg.setTemplateLoader(loader);

		TemplateRepresentation rep = null;
		final Map<String, Object> dataModel = new TreeMap<String, Object>();
		dataModel.put("device", device);

		rep = new TemplateRepresentation(template, cfg, dataModel,
				MediaType.APPLICATION_RDF_TURTLE);

		return rep;
	}

	private Device getDevice(String did) {
		String queryString = ""
				+ "PREFIX jhess: <http://jhess.googlecode.com/files/jhess.owl#>"
				+ " SELECT ?description ?location ?inputpower ?unit ?status ?start ?datacloud "
				+ " WHERE {"
				+ " jhess:"
				+ did
				+ " jhess:hasDescription ?description."
				+ " jhess:"
				+ did
				+ " jhess:hasLocation ?location. "
				+ " jhess:"
				+ did
				+ " jhess:hasInputPower ?inputpower. "
				+ " jhess:"
				+ did
				+ " jhess:hasInputPowerUnit  ?unit. "
				+ " jhess:"
				+ did
				+ " jhess:hasCurrentDeviceStatus ?status."
				+ " jhess:"
				+ did
				+ " jhess:hasStatusStartTime ?start. "
				+ " OPTIONAL { jhess:"
				+ did
				+ "  jhess:hasCurrentMeasureValue ?datacloud } OPTIONAL {jhess:"
				+ did
				+ "  jhess:hasCurrentTemperatureValue ?datacloud } OPTIONAL {jhess:"
				+ did
				+ "  jhess:hasCurrentPresenceValue ?datacloud } OPTIONAL {jhess:"
				+ did
				+ "  jhess:hasCurrentHumidityValue ?datacloud }}";
		QueryExecution qe = null;
		Device result = new Device();
		// Query
		ServletContext context = (ServletContext) getContext().getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");
		String ontology = context.getAttribute("ontology").toString();
		try {

			Model modelRDF = FileManager.get().loadModel(ontology + "hess.ttl");

			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, modelRDF);
			ResultSet rs = qe.execSelect();

			while (rs.hasNext()) {
				QuerySolution binding = rs.nextSolution();
				result.setId(did);
				result.setDescription(binding.getLiteral("description")
						.getString());
				result.setLocation(binding.getResource("location")
						.getLocalName());
				result.setInputPower(binding.getLiteral("inputpower").getInt());
				result.setInputPowerUnit(binding.getLiteral("unit").getString());
				result.setCurrentDeviceStatus(binding.getLiteral("status")
						.getString());
				result.setStatusStartTime(binding.getLiteral("start")
						.getString());
				if (binding.getLiteral("datacloud") != null) {
					result.setDataCloud(binding.getLiteral("datacloud")
							.getString());
				} else {
					result.setDataCloud(new String(""));

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qe.close();
		}

		return result;
	}

}
