package calculator;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface SubtractService {

	double subtract(double n1, double n2);

}