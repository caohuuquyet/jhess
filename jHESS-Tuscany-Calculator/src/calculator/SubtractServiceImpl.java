package calculator;

public class SubtractServiceImpl implements SubtractService {
	public double subtract(double n1, double n2) {
		System.out.println("SubtractService - devide " + n1 + " and " + n2);
		return n1 - n2;
	}
}