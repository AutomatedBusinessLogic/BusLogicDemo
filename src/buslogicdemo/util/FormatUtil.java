package buslogicdemo.util;

import java.math.BigDecimal;

/**
 * A few utility methods to format text and numbers.
 */
public class FormatUtil {

	/**
	 * Take a string and return it, having replaced all single quotes with %27 (i.e. URL-ized it)
	 */
	public static String escapeIdent(String s) {
		return s.replaceAll("'", "%27");
	}
	
	/**
	 * Parse a money amount, ignoring $'s and thousand-separating commas.
	 */
	public static BigDecimal parseMoney(String s) {
		BigDecimal result = BigDecimal.ZERO;
		String cleanValue = s.trim().replaceAll("\\$", "");
		cleanValue = cleanValue.replaceAll(",", "");
		try {
			result = new BigDecimal(cleanValue);
		}
		catch(Exception ex) {
			throw new RuntimeException("Invalid value: " + s);
		}
		if (result.compareTo(BigDecimal.ZERO) < 0)
			throw new RuntimeException("Negative value not allowed: " + s);
		
		return result;
	}
	
	/**
	 * Parse a number, ignoring thousand-separating commas. A negative number
	 * will cause an exception.
	 */
	public static Integer parseNumber(String s) {
		int result = 0;
		String cleanValue = s.trim().replaceAll(",", "");
		try {
			result = Integer.valueOf(cleanValue);
		}
		catch(Exception ex) {
			throw new RuntimeException("Invalid value: " + s);
		}
		if (result < 0)
			throw new RuntimeException("Negative value not allowed: " + s);
		
		return result;
	}
}
