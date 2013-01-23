package calculator;

public class MultiplyServiceImpl implements MultiplyService {
	

	@Override
	public double multiply(double n1, double n2) {
		System.out.println("MultiplyService * multiply " + n1 + " and " + n2);
		return n1 * n2;
	}
}