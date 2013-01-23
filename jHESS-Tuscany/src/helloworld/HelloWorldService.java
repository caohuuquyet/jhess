package helloworld;

import org.osoa.sca.annotations.Remotable;
@Remotable
public interface HelloWorldService {
	public String getGreetings(String name);
}
