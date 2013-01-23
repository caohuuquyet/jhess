package helloworldClient;
import org.osoa.sca.annotations.Reference;
public class WebServiceClient {
	@Reference
	private WebServiceInterface helloWorldService;
	public void use() {
	System.out.println(helloWorldService.getGreetings("BELAID"));
	}
}