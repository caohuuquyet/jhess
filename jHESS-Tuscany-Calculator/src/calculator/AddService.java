package calculator;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface AddService {

	double add(double n1, double n2);

}