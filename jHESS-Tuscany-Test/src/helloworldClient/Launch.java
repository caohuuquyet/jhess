package helloworldClient;
import org.apache.tuscany.sca.host.embedded.SCADomain;
public class Launch {
	public final static void main(String[] args) throws Exception {
	SCADomain scaDomain = SCADomain.newInstance("helloworldwsclient.composite");
	WebServiceClient use = scaDomain.getService(WebServiceClient.class,"WebServiceClient");
	use.use();
	scaDomain.close();
	}
}