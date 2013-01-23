package services;

public class CurrencyConverterImpl implements CurrencyConverter {
	public double getConversion(String fromCurrencyCode, String toCurrencyCode,
			double amount) {
		if (toCurrencyCode.equals("USD"))
			return amount;
		else if (toCurrencyCode.equals("EUR"))
			return ((double) Math.round(amount * 0.7256 * 100)) / 100;
		return 0;
	}

	public String getCurrencySymbol(String currencyCode) {
		if (currencyCode.equals("USD"))
			return "$";
		else if (currencyCode.equals("EUR"))
			return "E"; // "€";
		return "?";
	}
}