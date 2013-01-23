package helloworld;
import org.osoa.sca.annotations.Service;
@Service(HelloWorldService.class)
public class HelloWorldImpl implements HelloWorldService {
	public String getGreetings(String name) {
		return "Hello " + name;
	}
}
