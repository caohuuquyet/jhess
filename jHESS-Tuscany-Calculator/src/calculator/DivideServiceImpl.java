package calculator;

public class DivideServiceImpl implements DivideService {

	@Override
	public double divide(double n1, double n2) {
		System.out.println("DivideService / devide " + n1 + " and " + n2);
		double r = 0;
		try {
			r = n1 / n2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
}