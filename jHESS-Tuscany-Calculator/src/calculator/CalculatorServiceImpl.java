package calculator;

import org.osoa.sca.annotations.Reference;

public class CalculatorServiceImpl implements CalculatorService {
	@Reference
	private AddService addService;
	@Reference
	private SubtractService subtractService;
	@Reference
	private MultiplyService multiplyService;
	@Reference
	private DivideService divideService;

	public double add(double n1, double n2) {
		return addService.add(n1, n2);
	}

	public double subtract(double n1, double n2) {
		return subtractService.subtract(n1, n2);
	}

	public double multiply(double n1, double n2) {
		return multiplyService.multiply(n1, n2);
	}

	public double divide(double n1, double n2) {
		return divideService.divide(n1, n2);
	}
}