package helloworldClient;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface WebServiceInterface {
	public String getGreetings(String name);
} 