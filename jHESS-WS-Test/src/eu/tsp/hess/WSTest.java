package eu.tsp.hess;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
 
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Servlet implementation class WSTest
 */
public class WSTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WSTest() {
        super();
       
    }

    /**
	 * Processes requests for both HTTP
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		 // TODO Auto-generated constructor stub
        String HELLO_REST_URI = "http://localhost:8088/jHESS-WS"; 
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);  
     
        WebResource service = client.resource(UriBuilder.fromUri(HELLO_REST_URI).build()); 
     
        // Print service status
        response.getWriter().print(service.path("hello").accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString());
        // Print response as plain text
        response.getWriter().print(service.path("hello").accept(MediaType.TEXT_PLAIN).get(String.class));
        // Print response as HTML
        response.getWriter().print(service.path("hello").accept(MediaType.TEXT_HTML).get(String.class));
		
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

}
