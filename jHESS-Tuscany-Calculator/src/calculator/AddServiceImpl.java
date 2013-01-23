package calculator;

public class AddServiceImpl implements AddService {
	public double add(double n1, double n2) {
		System.out.println("AddService - add " + n1 + " and " + n2);
		return n1 + n2;
	}
}