package eu.tsp.hess;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Reference;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Literal;
import org.restlet.ext.rdf.internal.RdfConstants;
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

import eu.tsp.hess.dto.Device;

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
			jsonDevice.put("hasCurrentDeviceStatus", device.getCurrentDeviceStatus());
			jsonDevice.put("hasStatusStartTime", device.getStatusStartTime());
		}
		return new JsonRepresentation(jsonDevice);
	}

	@Get("rdf")	
	public Representation toRdf() throws ResourceException {
		String did = (String) getRequestAttributes().get("did");
		Device device = getDevice(did);
		Graph example = new Graph();

		if (device != null) {

			String ONT_BASE = ":";

			Reference id = new Reference(ONT_BASE + device.getId());
			Reference description = new Reference(ONT_BASE + "hasDescription");
			Reference location = new Reference(ONT_BASE + "hasLocation");
			Reference inputPower = new Reference(ONT_BASE + "hasInputPower");
			Reference inputPowerUnit = new Reference(ONT_BASE
					+ "hasInputPowerUnit");
			Reference currentDeviceStatus = new Reference(ONT_BASE
					+ "hasCurrentDeviceStatus");
			Reference statusStartTime = new Reference(ONT_BASE
					+ "hasStatusStartTime");

			// Reference dataCloud = new Reference(ONT_BASE + "hasDataCloud");

			example.add(id, description, new Literal(device.getDescription()));
			example.add(id, location, new Reference(device.getLocation()
					.toString()));
			example.add(id, inputPower, new Literal(
					"" + device.getInputPower(),
					RdfConstants.XML_SCHEMA_TYPE_INTEGER));
			example.add(id, inputPowerUnit,
					new Literal(device.getInputPowerUnit()));
			example.add(id, currentDeviceStatus,
					new Literal(device.getCurrentDeviceStatus()));
			example.add(id, statusStartTime,
					new Literal(device.getStatusStartTime(), new Reference(
							"http://www.w3.org/2001/XMLSchema#dateTime")));

		}

		return example.getRdfTurtleRepresentation();
	}
	
	
	private Device getDevice(String did) {

		String queryString = ""
				+ "PREFIX : <http://jhess.googlecode.com/files/jhess.owl#>"
				+ "SELECT ?description ?location ?inputpower ?unit ?status ?startime ?datacloud"
				+ "WHERE {" + ":" + did + " :hasDescription ?description."
				+ ":" + did + " :hasLocation ?location. " + ":" + did
				+ " :hasInputPower ?inputpower. " + ":" + did
				+ " :hasInputPowerUnit  ?unit. " + ":" + did
				+ " :hasCurrentDeviceStatus ?status." + ":" + did
				+ " :hasStatusStartTime ?startime. " + ":" + did
				+ " :hasHistoryData ?datacloud." + "}";
		QueryExecution qe = null;
		Device result = new Device();
		// Query
		try {
			ServletContext context = (ServletContext) getContext()
					.getAttributes().get(
							"org.restlet.ext.servlet.ServletContext");

			Object model = context.getAttribute("ontology");
			Query query = QueryFactory.create(queryString);
			qe = QueryExecutionFactory.create(query, (Model) model);
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
				result.setStatusStartTime(binding.getLiteral("startime")
						.getString());

			}

		} catch (Exception e) {

		} finally {
			qe.close();
		}

		return result;
	}

}
