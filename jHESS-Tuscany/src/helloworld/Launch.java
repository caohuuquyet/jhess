package helloworld;

import java.io.IOException;
import org.apache.tuscany.sca.host.embedded.SCADomain;
public class Launch {
	public static void main(String[] args) {
		SCADomain scaDomain = SCADomain.newInstance("helloworld.composite");
		try {
			System.out
					.println("HelloWorld server started (press enter to shutdown)");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scaDomain.close();
		System.out.println("HelloWorld server stopped");
	}
}
